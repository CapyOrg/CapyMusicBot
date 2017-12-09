package org.capy.musicbot.service;

import java.time.Instant;

public class ServiceResponse<ContentType> {

    private ContentType content;
    private Meta meta;

    ServiceResponse(ContentType content, boolean successful) {
        this.content = content;
        this.meta = new Meta(successful);
    }

    public ContentType getContent() {
        return content;
    }

    public Meta getMeta() {
        return meta;
    }

    public static class Meta {
        private long timestamp;
        private boolean successful;

        Meta(boolean successful) {
            this.successful = successful;
            timestamp = Instant.now().getEpochSecond();
        }

        public long getTimestamp() {
            return timestamp;
        }

        public boolean isSuccessful() {
            return successful;
        }
    }

}
