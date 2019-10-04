package com.example

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct
import javax.persistence.Entity
import javax.persistence.Id

@SpringBootApplication
class App

fun main(args: Array<String>) {
  SpringApplication.run(App::class.java, *args)
}

@Entity
data class Fruit(
        @Id val id: Long,
        val name: String,
        val color: String
)

interface FruitRepository: JpaRepository<Fruit, Long> {
    fun findFirstByColor(color: String): Optional<Fruit>

    @Query("select new Fruit(f.id, concat('delicious ', f.name), f.color) from Fruit f where name = :name")
    fun getDeliciousFruit(name: String): Fruit
}

@Component
class AppInitializator {
    val log: Logger = LoggerFactory.getLogger(AppInitializator::class.java)

    @Autowired
    lateinit var fruits: FruitRepository
 
    @PostConstruct
    private fun init() {
        log.info("** Delicious fruits: ${fruits.getDeliciousFruit("apple")} **")
    }

    @Bean
    fun repositoryPopulator(): Jackson2RepositoryPopulatorFactoryBean {
        val factory = Jackson2RepositoryPopulatorFactoryBean()
        factory.setResources(arrayOf(ClassPathResource("fruit-data.json")))

        return factory
    }
}
