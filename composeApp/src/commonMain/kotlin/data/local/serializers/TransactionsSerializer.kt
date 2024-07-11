package data.local.serializers

import androidx.datastore.core.okio.OkioSerializer
import com.mama1emon.exac.Transactions
import okio.BufferedSink
import okio.BufferedSource
import okio.IOException

object TransactionsSerializer : OkioSerializer<Transactions> {

    override val defaultValue: Transactions
        get() = Transactions(transactions = emptyList())

    override suspend fun readFrom(source: BufferedSource): Transactions {
        try {
            return Transactions.ADAPTER.decode(source)
        } catch (exception: IOException) {
            throw Exception(exception.message ?: "Serialization Exception")
        }
    }

    override suspend fun writeTo(t: Transactions, sink: BufferedSink) {
        sink.write(t.encode())
    }
}