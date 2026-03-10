package com.designlife.orchestrator.notification.store

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.designlife.orchestrator.data.NotificationInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object NotificationSerializer : Serializer<List<NotificationInfo>> {

    override val defaultValue: List<NotificationInfo> = emptyList()

    override suspend fun readFrom(input: InputStream): List<NotificationInfo> {
        return try {
            Json.decodeFromString(
                ListSerializer(NotificationInfo.serializer()),
                input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            throw CorruptionException("Unable to read Notification", e)
        }
    }

    override suspend fun writeTo(
        t: List<NotificationInfo>,
        output: OutputStream
    ) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(
                    ListSerializer(NotificationInfo.serializer()),
                    t
                ).encodeToByteArray()
            )
        }
    }
}