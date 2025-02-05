package com.aiany.core;

import com.aiany.core.log.LogLevel;
import com.aiany.core.request.ChatCompletionRequest;
import com.aiany.core.response.ChatCompletionResponse;
import com.aiany.core.serializable.DefaultGsonFactory;
import com.aiany.core.serializable.GsonFactory;
import com.aiany.core.spi.ServiceHelper;
import com.google.gson.Gson;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;

/**
 * @author waani
 */
public abstract class Client {

    public abstract ChatCompletionResponse chatCompletions(ChatCompletionRequest chatCompletionRequest);


    public static class Options {
        public String model;
        public String baseUrl = "https://api.openai.com/v1/";
        public String organizationId;
        public String apiVersion;
        public String apiKey;
        public Duration callTimeout = Duration.ofSeconds(60);
        public Duration connectTimeout = Duration.ofSeconds(60);
        public Duration readTimeout = Duration.ofSeconds(60);
        public Duration writeTimeout = Duration.ofSeconds(60);
        public Proxy proxy;
        public String userAgent;
        public boolean logRequests;
        public boolean logResponses;
        public LogLevel logLevel = LogLevel.DEBUG;
        public boolean logStreamingResponses;
        public Path persistTo;
        public Map<String, String> customHeaders;

        private Options() {
        }

        public static Options builder() {
            return new Options();
        }

        public Options build() {
            return this;
        }


        /**
         * @param model The model to use for the API request.
         *              For OpenAI (default): "text-davinci-003"
         *              For Azure OpenAI: "text-davinci-003"
         * @return builder
         */
        public Options model(String model) {
            if (model == null || model.trim().isEmpty()) {
                throw new IllegalArgumentException("model cannot be null or empty");
            }
            this.model = model;
            return this;
        }

        /**
         * @param baseUrl Base URL of OpenAI API.
         *                For OpenAI (default): "<a href="https://api.openai.com/v1/">...</a>"
         *                For Azure OpenAI: "https://{resource-name}.openai.azure.com/openai/deployments/{deployment-id}/"
         * @return builder
         */
        public Options baseUrl(String baseUrl) {
            if (baseUrl == null || baseUrl.trim().isEmpty()) {
                throw new IllegalArgumentException("baseUrl cannot be null or empty");
            }
            this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
            return this;
        }

        /**
         *
         * @param organizationId The organizationId for OpenAI:
         *                       <a href="https://platform.openai.com/docs/api-reference/organization-optional">...</a>
         * @return builder
         */
        public Options organizationId(String organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        /**
         * @param apiVersion Version of the API in the YYYY-MM-DD format. Applicable only for Azure OpenAI.
         * @return builder
         */
        public Options apiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
            return this;
        }


        public Options apiKey(String apiKey) {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("apiKey cannot be null or empty");
            }
            this.apiKey = apiKey;
            return this;
        }

        public Options callTimeout(Duration callTimeout) {
            if (callTimeout != null) {
                this.callTimeout = callTimeout;
            }
            return this;
        }

        public Options connectTimeout(Duration connectTimeout) {
            if (connectTimeout != null) {
                this.connectTimeout = connectTimeout;
            }
            return this;
        }

        public Options readTimeout(Duration readTimeout) {
            if (readTimeout != null) {
                this.readTimeout = readTimeout;
            }
            return this;
        }

        public Options writeTimeout(Duration writeTimeout) {
            if (writeTimeout != null) {
                this.writeTimeout = writeTimeout;
            }
            return this;
        }

        public Options proxy(Proxy.Type type, String ip, int port) {
            this.proxy = new Proxy(type, new InetSocketAddress(ip, port));
            return this;
        }

        public Options proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public Options userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Options logRequests() {
            return logRequests(true);
        }

        public Options logRequests(Boolean logRequests) {
            if (logRequests == null) {
                logRequests = false;
            }
            this.logRequests = logRequests;
            return this;
        }

        public Options logLevel(LogLevel logLevel) {
            if (logLevel == null) {
                logLevel = LogLevel.DEBUG;
            }
            this.logLevel = logLevel;
            return this;
        }

        public Options logResponses() {
            return logResponses(true);
        }

        public Options logResponses(Boolean logResponses) {
            if (logResponses == null) {
                logResponses = false;
            }
            this.logResponses = logResponses;
            return this;
        }

        public Options logStreamingResponses() {
            return logStreamingResponses(true);
        }

        public Options logStreamingResponses(Boolean logStreamingResponses) {
            if (logStreamingResponses == null) {
                logStreamingResponses = false;
            }
            this.logStreamingResponses = logStreamingResponses;
            return this;
        }

        /**
         * Generated response will be persisted under <code>java.io.tmpdir</code>. Used with images generation for the moment only.
         * The URL within <code>dev.ai4j.openai4j.image.GenerateImagesResponse</code> will contain the URL to local images then.
         *
         * @return builder
         */
        public Options withPersisting() {
            persistTo = Paths.get(System.getProperty("java.io.tmpdir"));
            return this;
        }

        /**
         * Generated response will be persisted under provided path. Used with images generation for the moment only.
         * The URL within <code>dev.ai4j.openai4j.image.GenerateImagesResponse</code> will contain the URL to local images then.
         *
         * @param persistTo path
         * @return builder
         */
        public Options persistTo(Path persistTo) {
            this.persistTo = persistTo;
            return this;
        }

        /**
         * Custom headers to be added to each HTTP request.
         *
         * @param customHeaders a map of headers
         * @return builder
         */
        public Options customHeaders(Map<String, String> customHeaders) {
            this.customHeaders = customHeaders;
            return this;
        }
    }


    public static Gson getGson() {
        for (GsonFactory loadFactory : ServiceHelper.loadFactories(GsonFactory.class)) {
            return loadFactory.get();
        }
        return new DefaultGsonFactory().get();
    }

}
