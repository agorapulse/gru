package com.agorapulse.gru;

public final class GlobalConfiguration {
    private String baseUri;

    private GlobalConfiguration(final String baseUri) {
        this.baseUri = baseUri;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public static final class GlobalConfigurationBuilder {
        private String baseUri;

        private GlobalConfigurationBuilder() {
        }

        public GlobalConfigurationBuilder withBaseUri(final String baseUri) {
            this.baseUri = baseUri;
            return this;
        }

        public GlobalConfiguration build() {
            return new GlobalConfiguration(baseUri);
        }
    }

    public static GlobalConfigurationBuilder defaults() {
        return new GlobalConfigurationBuilder();
    }


}
