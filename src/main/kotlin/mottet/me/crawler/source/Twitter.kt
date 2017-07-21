package mottet.me.crawler.source

import mottet.me.crawler.toReadableDate
import org.jsoup.Jsoup
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/twitter")
class TwitterController(val service: TwitterService) {

    @RequestMapping("/favorites")
    fun favorite() = "{\"current\" : ${service.currentLikes()}, \"lastUpdated\" : " +
            "\"${service.lastUpdateDateTime().toReadableDate()}\" }"

    @RequestMapping("/followers")
    fun followers() = "{\"current\" : ${service.currentFollowers()}, \"lastUpdated\" : " +
            "\"${service.lastUpdateDateTime().toReadableDate()}\" }"

    @RequestMapping("/last")
    fun last30Days() = service.last30days()

}

@Service
class TwitterService(val repository: TwitterRepository) {
    private var favorites = 0
    private var followers = 0
    private var lastUpdated = LocalDateTime.now()!!

    fun currentLikes() = favorites
    fun currentFollowers() = followers
    fun lastUpdateDateTime() = lastUpdated
    fun last30days() = repository.findTop30ByOrderByDateDesc().map { it.followers } + currentFollowers()

    @Scheduled(fixedDelay = 700_000, initialDelay = 0)
    fun fetch() {
        favorites = fetch("[data-nav='favorites'] .ProfileNav-value").toInt()
        followers = fetch("[data-nav='followers'] .ProfileNav-value").toInt()
        lastUpdated = LocalDateTime.now()
    }

    @Scheduled(cron = "0 59 23 * * ?")
    fun saveTwitterData() {
        repository.save(Twitter(date = LocalDate.now(), favorites = favorites, followers = followers))
    }

    private fun fetch(css: String) = Jsoup.connect("https://twitter.com/pupscan")
            .get()
            .select(css)
            .text()!!
}

@Document
class Twitter(@Id val id: String = UUID.randomUUID().toString(),
              @Indexed val date: LocalDate,
              val favorites: Int,
              val followers: Int)

interface TwitterRepository : CrudRepository<Twitter, String> {
    fun findTop30ByOrderByDateDesc(): List<Twitter>
}