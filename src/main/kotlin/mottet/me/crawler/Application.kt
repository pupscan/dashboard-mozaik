package mottet.me.crawler

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@EnableScheduling
@SpringBootApplication
class CrawlerApplication

fun main(args: Array<String>) {
    SpringApplication.run(CrawlerApplication::class.java, *args)
}

/**
 * Util
 */
fun Int.toReadableNumber() = DecimalFormat("#,###", DecimalFormatSymbols(Locale.FRANCE)).format(this)!!
fun now() = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))!!

