/*
 * Copyright (c) 2024 Robert Jaros
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.kilua.ssr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import dev.kilua.core.IComponent
import dev.kilua.routing.RoutingBuilder
import dev.kilua.routing.RoutingBuilderImpl
import dev.kilua.routing.RoutingContext
import dev.kilua.routing.RoutingContextImpl
import dev.kilua.routing.RoutingDsl
import dev.kilua.routing.RoutingModel
import dev.kilua.routing.RoutingModelCompositionLocal

/**
 * A router supporting Server-Side Rendering (SSR).
 *
 * When declaring routes using [RoutingBuilder.action] function, the rendering on the server will be
 * deferred until if an action is executed.
 *
 * @param initRoute the initial route to navigate to (default is "/")
 * @param contextPath the context path to prepend to the initial route
 * @param active whether the router is active (default is true)
 * @param stateSerializer an optional serializer for the state, used for SSR
 * @param routing the routing configuration block
 */
@Composable
public fun IComponent.ssrRouter(
    initRoute: String = "/",
    contextPath: String = getContextPath(),
    active: Boolean = true,
    stateSerializer: (() -> String)? = null,
    routing: @RoutingDsl RoutingBuilder.(RoutingContext) -> Unit
) {
    val routingBuilder = RoutingBuilderImpl()
    val routingContext = RoutingContextImpl()
    routingBuilder.routing(routingContext)
    val routingModel = routingBuilder.getRoutingModel()
    CompositionLocalProvider(RoutingModelCompositionLocal provides routingModel) {
        if (renderConfig.isDom) {
            GlobalSsrRouter("$contextPath$initRoute", active, useDoneCallback = true) {
                if (contextPath.isNotEmpty()) {
                    route("$contextPath$initRoute") {
                        routingContext.apply {
                            path = this@route.path
                            parameters = this@route.parameters
                        }
                        routingModel.apply(this@ssrRouter, this, routingContext, routingModel.defaultMeta)
                    }
                } else {
                    routingContext.apply {
                        path = this@GlobalSsrRouter.path
                        parameters = this@GlobalSsrRouter.parameters
                    }
                    routingModel.apply(this@ssrRouter, this, routingContext, routingModel.defaultMeta)
                }
            }
        } else {
            GlobalSsrRouter(initRoute, active, stateSerializer, useDoneCallback = true) {
                routingContext.apply {
                    path = this@GlobalSsrRouter.path
                    parameters = this@GlobalSsrRouter.parameters
                }
                routingModel.apply(this@ssrRouter, this, routingContext, routingModel.defaultMeta)
            }
        }
    }
    RoutingModel.globalRoutingModel = routingModel
}
