package com.anddevcorp.channelexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class MainActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button1: Button = findViewById(R.id.button)
        val textView1: TextView = findViewById(R.id.textView2)

        val button2: Button = findViewById(R.id.button2)
        val textView2: TextView = findViewById(R.id.textView)

        val button3: Button = findViewById(R.id.button3)
        val textView3: TextView = findViewById(R.id.textView3)

        val button4: Button = findViewById(R.id.button4)
        val textView4: TextView = findViewById(R.id.textView4)

        // 1. Rendezvous Channel Örneği
        button1.setOnClickListener {
            val channel = Channel<String>(Channel.RENDEZVOUS)
            scope.launch {
                channel.send("Rendezvous Channel: Message sent!")
            }

            scope.launch {
                delay(3000)
                val result1 = channel.receive()
                textView1.text = result1
            }
        }


        // 2. CONFLATED Channel Örneği
        button2.setOnClickListener {
            val channel = Channel<String>(Channel.CONFLATED)
            scope.launch {
                channel.send("Conflated (Broadcast) Channel: Message sent!")
                channel.send("Conflated (Broadcast) Channel: Message2 sent!")
                channel.send("Conflated (Broadcast) Channel: Message3 sent!")

            }
            scope.launch {
                val result = channel.receive()
                textView2.text = result
                delay(1500)
            }
        }
        // 3. Unlimited Channel Örneği
        button3.setOnClickListener {
            val channel = Channel<String>(Channel.UNLIMITED)
            scope.launch {
                channel.send("Unlimited Channel: Message sent!")
                channel.send("Unlimited Channel: Message2 sent!")
                channel.send("Unlimited Channel: Message3 sent!")
                channel.send("Unlimited Channel: Message4 sent!")
                repeat(4) {
                    val result = channel.receive()
                    textView3.text = result
                    delay(1500)
                }
            }
        }

        // 4. Buffer Channel Örneği (Buffer)
        button4.setOnClickListener {
            val channel = Channel<String>(3) // Buffer kapasitesi 3
            scope.launch {
                // 4 mesaj gönderiyoruz, 3'ü buffer'a sığacak, 4. mesaj bekleyecek
                channel.send("Buffered Channel: Message1 sent!")
                channel.send("Buffered Channel: Message2 sent!")
                channel.send("Buffered Channel: Message3 sent!")
                channel.send("Buffered Channel: Message4 sent!")

            }
            scope.launch {
                repeat(3) {
                    val result = channel.receive()
                    textView4.append("\n" + result)
                    delay(1500)
                }
            }
        }


    }

    // Aktivite yok edilirken coroutine'leri iptal ediyoruz
    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
