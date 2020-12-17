package com.icuxika.api;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * 对RequestBody进行一层封装以获取OkHttp请求上传进度
 */
public class ObservableRequestBody extends RequestBody {

    private final RequestBody delegate;

    private final RequestProgressListener listener;

    public ObservableRequestBody(RequestBody delegate) {
        this.delegate = delegate;
        this.listener = null;
    }

    public ObservableRequestBody(RequestBody delegate, RequestProgressListener listener) {
        this.delegate = delegate;
        this.listener = listener;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public void writeTo(@NotNull BufferedSink sink) throws IOException {
        ObservableSink observableSink = new ObservableSink(sink);
        BufferedSink bufferedSink = Okio.buffer(observableSink);
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    @Override
    public long contentLength() throws IOException {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            return -1;
        }
    }

    protected final class ObservableSink extends ForwardingSink {

        private long workDone;

        public ObservableSink(@NotNull Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NotNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            workDone += byteCount;
            if (listener != null) {
                listener.onRequestProgress(workDone, contentLength());
            }
        }
    }
}
