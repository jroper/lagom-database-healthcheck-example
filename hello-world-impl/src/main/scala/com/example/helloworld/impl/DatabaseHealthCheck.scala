package com.example.helloworld.impl

import akka.actor.ActorSystem
import akka.management.scaladsl.HealthChecks.HealthCheck

import scala.concurrent.Future

class DatabaseHealthCheck(system: ActorSystem) extends HealthCheck {
  import system.dispatcher

  override def apply(): Future[Boolean] = {
    HelloWorld(system).get match {

      case None => Future.successful(false)

      case Some(app) =>

        import app.slickProvider.profile.api._

        app.slickProvider.db.run(sql"select 1;".as[Int])
          .map(_ => true)
    }
  }
}
