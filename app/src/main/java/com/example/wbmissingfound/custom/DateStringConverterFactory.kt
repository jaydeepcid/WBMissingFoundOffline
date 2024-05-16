package com.example.wbmissingfound.custom

import okhttp3.RequestBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.Query
import java.io.IOException
import java.lang.reflect.Type

class DateStringConverterFactory(private val delegateFactory: Converter.Factory) : Converter.Factory() {

    override fun stringConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? {
        for (annotation in annotations) {
            if (annotation is Query) {
                val delegate: Converter<*, RequestBody> =
                    delegateFactory.requestBodyConverter(type, annotations, arrayOf(), retrofit)!!
                return DelegateToStringConverter(delegate)
            }
        }
        return null
    }

    internal class DelegateToStringConverter<T>(private val delegate: Converter<T, RequestBody>) :
        Converter<T, String> {
        @Throws(IOException::class)
        override fun convert(value: T): String {
            val buffer = okio.Buffer()
            delegate.convert(value)?.writeTo(buffer)
            return if (value is String) {
                value
            } else {
                buffer.readUtf8()
            }
        }
    }
}
