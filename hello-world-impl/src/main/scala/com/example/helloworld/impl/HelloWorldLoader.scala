package com.example.helloworld.impl

import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.example.helloworld.api.HelloWorldService
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.softwaremill.macwire._
import play.api.db.HikariCPComponents

class HelloWorldLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new HelloWorldApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new HelloWorldApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[HelloWorldService])
}

abstract class HelloWorldApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with JdbcPersistenceComponents
    with HikariCPComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Make sure we set the application in the HelloWorld extension here
  HelloWorld(actorSystem).set(this)

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[HelloWorldService](wire[HelloWorldServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = HelloWorldSerializerRegistry

  // Register the Hello World persistent entity
  persistentEntityRegistry.register(wire[HelloWorldEntity])
}

class HelloWorldExtension extends Extension {
  @volatile private var application: Option[HelloWorldApplication] = None
  private[impl] def set(app: HelloWorldApplication) = application = Some(app)
  def get: Option[HelloWorldApplication] = application
}

object HelloWorld
  extends ExtensionId[HelloWorldExtension]
    with ExtensionIdProvider {

  override def lookup = HelloWorld

  //This method will be called by Akka
  // to instantiate our Extension
  override def createExtension(system: ExtendedActorSystem) = new HelloWorldExtension
}
