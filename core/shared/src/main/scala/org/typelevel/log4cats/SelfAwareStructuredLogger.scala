/*
 * Copyright 2018 Christopher Davenport
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.typelevel.log4cats

import cats._

trait SelfAwareStructuredLogger[F[_]] extends SelfAwareLogger[F] with StructuredLogger[F] {
  override def mapK[G[_]](fk: F ~> G): SelfAwareStructuredLogger[G] =
    SelfAwareStructuredLogger.mapK(fk)(this)
}

object SelfAwareStructuredLogger {
  def apply[F[_]](implicit ev: SelfAwareStructuredLogger[F]): SelfAwareStructuredLogger[F] = ev

  def withContext[F[_]](
      sl: SelfAwareStructuredLogger[F]
  )(ctx: Map[String, String]): SelfAwareStructuredLogger[F] =
    new ExtraContextSelfAwareStructuredLogger[F](sl)(ctx)

  private class ExtraContextSelfAwareStructuredLogger[F[_]](sl: SelfAwareStructuredLogger[F])(
      ctx: Map[String, String]
  ) extends SelfAwareStructuredLogger[F] {
    private val outer = ctx
    def error(message: => String): F[Unit] = sl.error(outer)(message)
    def warn(message: => String): F[Unit] = sl.warn(outer)(message)
    def info(message: => String): F[Unit] = sl.info(outer)(message)
    def debug(message: => String): F[Unit] = sl.debug(outer)(message)
    def trace(message: => String): F[Unit] = sl.trace(outer)(message)
    def trace(ctx: Map[String, String])(msg: => String): F[Unit] =
      sl.trace(outer ++ ctx)(msg)
    def debug(ctx: Map[String, String])(msg: => String): F[Unit] =
      sl.debug(outer ++ ctx)(msg)
    def info(ctx: Map[String, String])(msg: => String): F[Unit] =
      sl.info(outer ++ ctx)(msg)
    def warn(ctx: Map[String, String])(msg: => String): F[Unit] =
      sl.warn(outer ++ ctx)(msg)
    def error(ctx: Map[String, String])(msg: => String): F[Unit] =
      sl.error(outer ++ ctx)(msg)

    def isTraceEnabled: F[Boolean] = sl.isTraceEnabled
    def isDebugEnabled: F[Boolean] = sl.isDebugEnabled
    def isInfoEnabled: F[Boolean] = sl.isInfoEnabled
    def isWarnEnabled: F[Boolean] = sl.isWarnEnabled
    def isErrorEnabled: F[Boolean] = sl.isErrorEnabled

    def error(t: Throwable)(message: => String): F[Unit] =
      sl.error(outer, t)(message)
    def warn(t: Throwable)(message: => String): F[Unit] =
      sl.warn(outer, t)(message)
    def info(t: Throwable)(message: => String): F[Unit] =
      sl.info(outer, t)(message)
    def debug(t: Throwable)(message: => String): F[Unit] =
      sl.debug(outer, t)(message)
    def trace(t: Throwable)(message: => String): F[Unit] =
      sl.trace(outer, t)(message)

    def error(ctx: Map[String, String], t: Throwable)(message: => String): F[Unit] =
      sl.error(outer ++ ctx, t)(message)
    def warn(ctx: Map[String, String], t: Throwable)(message: => String): F[Unit] =
      sl.warn(outer ++ ctx, t)(message)
    def info(ctx: Map[String, String], t: Throwable)(message: => String): F[Unit] =
      sl.info(outer ++ ctx, t)(message)
    def debug(ctx: Map[String, String], t: Throwable)(message: => String): F[Unit] =
      sl.debug(outer ++ ctx, t)(message)
    def trace(ctx: Map[String, String], t: Throwable)(message: => String): F[Unit] =
      sl.trace(outer ++ ctx, t)(message)
  }

  private def mapK[G[_], F[_]](
      f: G ~> F
  )(logger: SelfAwareStructuredLogger[G]): SelfAwareStructuredLogger[F] =
    new SelfAwareStructuredLogger[F] {
      def isTraceEnabled: F[Boolean] =
        f(logger.isTraceEnabled)
      def isDebugEnabled: F[Boolean] =
        f(logger.isDebugEnabled)
      def isInfoEnabled: F[Boolean] =
        f(logger.isInfoEnabled)
      def isWarnEnabled: F[Boolean] =
        f(logger.isWarnEnabled)
      def isErrorEnabled: F[Boolean] =
        f(logger.isErrorEnabled)

      def trace(ctx: Map[String, String])(msg: => String): F[Unit] =
        f(logger.trace(ctx)(msg))
      def debug(ctx: Map[String, String])(msg: => String): F[Unit] =
        f(logger.debug(ctx)(msg))
      def info(ctx: Map[String, String])(msg: => String): F[Unit] =
        f(logger.info(ctx)(msg))
      def warn(ctx: Map[String, String])(msg: => String): F[Unit] =
        f(logger.warn(ctx)(msg))
      def error(ctx: Map[String, String])(msg: => String): F[Unit] =
        f(logger.error(ctx)(msg))

      def error(t: Throwable)(message: => String): F[Unit] =
        f(logger.error(t)(message))
      def warn(t: Throwable)(message: => String): F[Unit] =
        f(logger.warn(t)(message))
      def info(t: Throwable)(message: => String): F[Unit] =
        f(logger.info(t)(message))
      def debug(t: Throwable)(message: => String): F[Unit] =
        f(logger.debug(t)(message))
      def trace(t: Throwable)(message: => String): F[Unit] =
        f(logger.trace(t)(message))
      def error(message: => String): F[Unit] =
        f(logger.error(message))
      def warn(message: => String): F[Unit] =
        f(logger.warn(message))
      def info(message: => String): F[Unit] =
        f(logger.info(message))
      def debug(message: => String): F[Unit] =
        f(logger.debug(message))
      def trace(message: => String): F[Unit] =
        f(logger.trace(message))

      def trace(ctx: Map[String, String], t: Throwable)(msg: => String): F[Unit] =
        f(logger.trace(ctx, t)(msg))
      def debug(ctx: Map[String, String], t: Throwable)(msg: => String): F[Unit] =
        f(logger.debug(ctx, t)(msg))
      def info(ctx: Map[String, String], t: Throwable)(msg: => String): F[Unit] =
        f(logger.info(ctx, t)(msg))
      def warn(ctx: Map[String, String], t: Throwable)(msg: => String): F[Unit] =
        f(logger.warn(ctx, t)(msg))
      def error(ctx: Map[String, String], t: Throwable)(msg: => String): F[Unit] =
        f(logger.error(ctx, t)(msg))
    }
}
