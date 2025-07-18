/*
 * Copyright (c) 2023 Robert Jaros
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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import app.softwork.routingcompose.Router
import dev.kilua.Application
import dev.kilua.LocalResource
import dev.kilua.animation.MotionTransition
import dev.kilua.animation.TransitionType
import dev.kilua.animation.animateColorAsState
import dev.kilua.animation.animateCssSizeAsState
import dev.kilua.animation.motionAnimatedVisibility
import dev.kilua.compose.adaptive.Breakpoint
import dev.kilua.compose.adaptive.TailwindcssBreakpoint
import dev.kilua.compose.adaptive.WindowWidthSizeClass
import dev.kilua.compose.adaptive.currentWindowSizeClass
import dev.kilua.compose.adaptive.rememberBreakpoint
import dev.kilua.compose.adaptive.rememberOrientation
import dev.kilua.compose.adaptive.rememberTailwindcssBreakpoint
import dev.kilua.compose.foundation.layout.Arrangement
import dev.kilua.compose.foundation.layout.Box
import dev.kilua.compose.foundation.layout.Row
import dev.kilua.compose.root
import dev.kilua.compose.ui.Alignment
import dev.kilua.compose.ui.Modifier
import dev.kilua.compose.ui.background
import dev.kilua.compose.ui.border
import dev.kilua.compose.ui.className
import dev.kilua.compose.ui.clickable
import dev.kilua.compose.ui.display
import dev.kilua.compose.ui.fillMaxWidth
import dev.kilua.compose.ui.height
import dev.kilua.compose.ui.id
import dev.kilua.compose.ui.onEvent
import dev.kilua.compose.ui.size
import dev.kilua.compose.ui.title
import dev.kilua.compose.ui.width
import dev.kilua.core.IComponent
import dev.kilua.dropdown.dropDown
import dev.kilua.externals.JsArray
import dev.kilua.externals.animateMini
import dev.kilua.externals.leaflet.geo.LatLng
import dev.kilua.externals.leaflet.layer.FeatureGroup
import dev.kilua.form.Autocomplete
import dev.kilua.form.EnumMask
import dev.kilua.form.ImaskOptions
import dev.kilua.form.MaskAutofix
import dev.kilua.form.NumberMask
import dev.kilua.form.PatternMask
import dev.kilua.form.RangeMask
import dev.kilua.form.check.checkBox
import dev.kilua.form.check.checkBoxRef
import dev.kilua.form.fieldWithLabel
import dev.kilua.form.form
import dev.kilua.form.formRef
import dev.kilua.form.number.imaskNumeric
import dev.kilua.form.number.numeric
import dev.kilua.form.number.range
import dev.kilua.form.number.rangeRef
import dev.kilua.form.select.TomSelectCallbacks
import dev.kilua.form.select.TomSelectRenders
import dev.kilua.form.select.select
import dev.kilua.form.select.tomSelect
import dev.kilua.form.select.tomSelectRef
import dev.kilua.form.text.password
import dev.kilua.form.text.richTextRef
import dev.kilua.form.text.text
import dev.kilua.form.text.textRef
import dev.kilua.form.text.tomTypeaheadRef
import dev.kilua.form.time.HourCycle
import dev.kilua.form.time.richDate
import dev.kilua.form.time.richDateTimeRef
import dev.kilua.form.time.richTime
import dev.kilua.html.*
import dev.kilua.html.helpers.TagStyleFun.Companion.background
import dev.kilua.html.helpers.TagStyleFun.Companion.border
import dev.kilua.html.helpers.onClickLaunch
import dev.kilua.html.helpers.onCombineClick
import dev.kilua.html.style.PClass
import dev.kilua.html.style.globalStyle
import dev.kilua.html.style.pClass
import dev.kilua.html.style.pElement
import dev.kilua.html.style.style
import dev.kilua.i18n.I18n
import dev.kilua.i18n.LocaleManager
import dev.kilua.i18n.SimpleLocale
import dev.kilua.maps.DefaultTileLayers
import dev.kilua.maps.LeafletObjectFactory
import dev.kilua.maps.maps
import dev.kilua.modal.FullscreenMode
import dev.kilua.modal.ModalSize
import dev.kilua.modal.alert
import dev.kilua.modal.confirm
import dev.kilua.modal.modal
import dev.kilua.modal.modalRef
import dev.kilua.panel.*
import dev.kilua.popup.Placement
import dev.kilua.popup.Trigger
import dev.kilua.popup.disableTooltip
import dev.kilua.popup.enableTooltip
import dev.kilua.popup.popover
import dev.kilua.popup.toggleTooltip
import dev.kilua.popup.tooltip
import dev.kilua.progress.Progress
import dev.kilua.progress.ProgressOptions
import dev.kilua.rest.HttpMethod
import dev.kilua.rest.RemoteRequestException
import dev.kilua.rest.RestClient
import dev.kilua.rest.RestResponse
import dev.kilua.rest.call
import dev.kilua.rest.callDynamic
import dev.kilua.rest.requestDynamic
import dev.kilua.routing.Meta
import dev.kilua.routing.RoutingModel
import dev.kilua.routing.global
import dev.kilua.routing.hashRouter
import dev.kilua.state.collectAsState
import dev.kilua.svg.path
import dev.kilua.svg.svg
import dev.kilua.tabulator.ColumnDefinition
import dev.kilua.tabulator.Formatter
import dev.kilua.tabulator.Layout
import dev.kilua.tabulator.PaginationMode
import dev.kilua.tabulator.ResponsiveLayout
import dev.kilua.tabulator.TableType
import dev.kilua.tabulator.TabulatorOptions
import dev.kilua.tabulator.tabulator
import dev.kilua.theme.ThemeManager
import dev.kilua.theme.themeSwitcher
import dev.kilua.toast.ToastPosition
import dev.kilua.toast.toast
import dev.kilua.toastify.ToastType
import dev.kilua.useModule
import dev.kilua.utils.KiluaScope
import dev.kilua.utils.cast
import dev.kilua.utils.jsArrayOf
import dev.kilua.utils.jsGet
import dev.kilua.utils.jsObjectOf
import dev.kilua.utils.now
import dev.kilua.utils.obj
import dev.kilua.utils.promise
import dev.kilua.utils.rem
import dev.kilua.utils.toJsArray
import dev.kilua.utils.toList
import dev.kilua.utils.today
import dev.kilua.utils.unsafeCast
import js.core.JsAny
import js.core.JsPrimitives.toJsInt
import js.core.JsPrimitives.toJsString
import js.import.JsModule
import js.json.parse
import js.promise.Promise
import js.promise.invoke
import js.regexp.RegExp
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import web.console.console
import web.dom.Text
import web.events.CustomEvent
import web.events.Event
import web.events.EventType
import web.events.addEventListener
import web.events.removeEventListener
import web.html.HTMLElement
import web.timers.setTimeout
import web.uievents.MouseEvent
import web.window.window
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextUInt
import kotlin.time.Duration.Companion.seconds

@JsModule("/kotlin/modules/json/test.json")
external object testJson : LocalResource

@JsModule("/kotlin/modules/i18n/messages-de.po")
external object messagesDe : JsAny

@JsModule("/kotlin/modules/i18n/messages-en.po")
external object messagesEn : JsAny

@JsModule("/kotlin/modules/i18n/messages-es.po")
external object messagesEs : JsAny

@JsModule("/kotlin/modules/i18n/messages-fr.po")
external object messagesFr : JsAny

@JsModule("/kotlin/modules/i18n/messages-ja.po")
external object messagesJa : JsAny

@JsModule("/kotlin/modules/i18n/messages-ko.po")
external object messagesKo : JsAny

@JsModule("/kotlin/modules/i18n/messages-pl.po")
external object messagesPl : JsAny

@JsModule("/kotlin/modules/i18n/messages-ru.po")
external object messagesRu : JsAny

@JsModule("/kotlin/modules/css/style.css")
external object css

@Serializable
data class Query(val q: String?)

@Serializable
data class SearchResult(val total_count: Int, val incomplete_results: Boolean)

@Serializable
data class Person(val name: String, val age: Int, val city: String)

@Serializable
data class FormData(val name: String? = null, val lname: String? = null, val date: LocalDate? = null)

@Serializable(with = ObjectIdSerializer::class)
class ObjectId(val id: Int) {
    override fun toString(): String {
        return "$id"
    }
}

object ObjectIdSerializer : KSerializer<ObjectId> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ObjectId")
    override fun deserialize(decoder: Decoder): ObjectId {
        val str = decoder.decodeString()
        return ObjectId(str.toInt())
    }

    override fun serialize(encoder: Encoder, value: ObjectId) {
        encoder.encodeString(value.toString())
    }
}

@Serializable
data class LoginForm(
    val username: String? = null,
    val password: String? = null,
    val rememberMe: Boolean = false,
    val test: ObjectId? = null
)

val i18n = I18n(
    "de" to messagesDe,
    "en" to messagesEn,
    "es" to messagesEs,
    "fr" to messagesFr,
    "ja" to messagesJa,
    "ko" to messagesKo,
    "pl" to messagesPl,
    "ru" to messagesRu
)

external class MoveInfo : JsAny {
    val from: Int
    val to: Int
}

@Serializable
data class Repository(val id: Int, val full_name: String?, val description: String?, val fork: Boolean)

class App : Application() {

    init {
        useModule(css)
    }

    override fun start() {

        console.log(testJson.content.jsGet("test"))

        ThemeManager.init()

        root("root") {

            div {

                val carousel = carouselRef(fade = true) {
                    item("First slide", "First slide label") {
                        div("d-block w-100") {
                            height(200.px)
                            background(Color.Red)
                        }
                    }
                    item("Second slide", "Second slide label") {
                        div("d-block w-100") {
                            height(200.px)
                            background(Color.Green)
                        }
                    }
                    item("Third slide", "Third slide label") {
                        div("d-block w-100") {
                            height(200.px)
                            background(Color.Blue)
                        }
                    }
                }
                button("Play") {
                    onClick {
                        carousel.cycle()
                    }
                }
                button("Pause") {
                    onClick {
                        carousel.pause()
                    }
                }

                hr()

                accordion(flush = true, alwaysOpen = true, openedIndex = 1) {
                    item("Google", "fab fa-google") {
                        +"Google is a technology company that specializes in Internet-related services and products."
                    }
                    item("Apple", "fab fa-apple") {
                        +"Apple Inc. is a technology company that designs, manufactures, and markets consumer electronics, computer software, and online services."
                    }
                    item("Microsoft", "fab fa-microsoft") {
                        +"Microsoft Corporation is a technology company that produces computer software, consumer electronics, personal computers, and related services."
                    }
                }

                hr()

                val offcanvas = offcanvasRef(
                    "Some caption",
                    placement = OffPlacement.OffcanvasEnd,
                    responsiveType = OffResponsiveType.OffcanvasLg,
                    closeButton = false,
                    bodyScrolling = true,
                    backdrop = false,
                    escape = false,
                ) {
                    p {
                        +"This is an offcanvas example."
                    }
                }
                button("Open offcanvas", className = "d-lg-none") {
                    onClick {
                        offcanvas.toggle()
                    }
                }

                hr()

                val tabs2 = mutableStateListOf<Triple<String, String, @Composable IComponent.() -> Unit>>(
                    Triple("Apple", "fab fa-apple", {
                        div {
                            +"Apple description"
                        }
                    }), Triple("Google", "fab fa-google", {
                        div {
                            +"Google description"
                        }
                    }), Triple("Microsoft", "fab fa-microsoft", {
                        div {
                            +"Microsoft description"
                        }
                    })
                )

                tabPanel(draggableTabs = true) {
                    tabs2.forEach { (title, icon, content) ->
                        tab(title, icon) {
                            content()
                        }
                    }
                    onEvent<CustomEvent<*>>("moveTab") {
                        val info = parse<MoveInfo>(it.detail.toString())
                        if (info.from < info.to) {
                            tabs2.add(info.to + 1, tabs2[info.from])
                            tabs2.removeAt(info.from)
                        } else {
                            tabs2.add(info.to, tabs2[info.from])
                            tabs2.removeAt(info.from + 1)
                        }
                    }
                }

                hr()

                bsButton("Click me", style = ButtonStyle.BtnSuccess, size = ButtonSize.BtnLg) {
                    onClick {
                        console.log("Hello Bootstrap!")
                    }
                }

                div("${BsBorder.Border} ${BsBorder.BorderDanger} ${BsRounded.RoundedPill} ${BsColor.TextBgInfo}") {
                    width(300.px)
                    padding(10.px)
                    +"Hello Bootstrap!"
                }

                hr()

                val restClient = RestClient()

                button("Rest") {
                    onClickLaunch {
                        val result: JsAny? = restClient.callDynamic("https://api.github.com/search/repositories") {
                            data = jsObjectOf("q" to "kilua")

                        }
                        console.log(result)
                        val items: List<Repository> = restClient.call("https://api.github.com/search/repositories") {
                            data = jsObjectOf("q" to "kilua")
                            resultTransform = { it?.jsGet("items") }
                        }
                        println(items)

                        val result2: JsAny? =
                            restClient.callDynamic("https://api.github.com/search/repositories", Query("kilua"))
                        console.log(result2)

                        val searchResult: SearchResult =
                            restClient.call("https://api.github.com/search/repositories", Query("kilua")) {
                                method = HttpMethod.Post
                                contentType = "application/json"
                                headers = {
                                    listOf(
                                        "User-Agent" to "KiluaApp"
                                    )
                                }
                            }
                        println(searchResult)

                        val restResponse: RestResponse<JsAny> =
                            restClient.requestDynamic("https://api.github.com/search/repositories") {
                                data = jsObjectOf("q" to "kilua")
                                headers = {
                                    listOf(
                                        "User-Agent" to "KiluaApp"
                                    )
                                }
                            }
                        println(restResponse.response.headers.get("Content-Type"))
                    }
                }

                hr()

                div {
                    border(1.px, BorderStyle.Solid, Color.Black)
                    width(100.px)
                    height(100.px)
                    setDragDropData("text/plain", "Element")
                }

                div {
                    background(Color.Lightgray)
                    border(1.px, BorderStyle.Solid, Color.Black)
                    width(200.px)
                    height(200.px)
                    setDropTargetData("text/plain") { data ->
                        println("Dropped data: $data")
                    }
                }

                hr()

                form {
                    fieldWithLabel("Username") {
                        text(id = it) {
                            bind("username")
                        }
                    }
                    fieldWithLabel("Password") {
                        password(id = it) {
                            bind("password")
                        }
                    }
                    button("Login") {
                        onClick {
                            val map = this@form.getData()
                            println("Username: ${map["username"]}")
                            println("Password: ${map["password"]}")
                        }
                    }
                }

                form<LoginForm> {
                    val validation by validationStateFlow.collectAsState()

                    vPanel(gap = 5.px) {
                        if (validation.isInvalid && validation.invalidMessage != null) {
                            div {
                                color(Color.Red)
                                +validation.invalidMessage!!
                            }
                        }
                        maxWidth(400.px)
                        hPanel(justifyContent = JustifyContent.SpaceBetween, gap = 15.px) {
                            fieldWithLabel("Username") {
                                text(id = it, required = true) {
                                    bindWithValidationMessage(LoginForm::username) {
                                        (it.value != null && it.value!!.length <= 20) to "Maximum length is 20 characters"
                                    }
                                }
                            }
                        }
                        val validationUsername = validation[LoginForm::username]
                        if (validationUsername?.isInvalid == true || validationUsername?.isEmptyWhenRequired == true) {
                            div {
                                color(Color.Red)
                                if (validationUsername.isEmptyWhenRequired) {
                                    +"Username is required"
                                } else if (validationUsername.invalidMessage != null) {
                                    +validationUsername.invalidMessage!!
                                }
                            }
                        }
                        hPanel(justifyContent = JustifyContent.SpaceBetween, gap = 15.px) {
                            fieldWithLabel("Password") {
                                password(id = it, required = true) {
                                    bind(LoginForm::password)
                                }
                            }
                        }
                        val validationPassword = validation[LoginForm::password]
                        if (validationPassword?.isInvalid == true || validationPassword?.isEmptyWhenRequired == true) {
                            div {
                                color(Color.Red)
                                if (validationPassword.isEmptyWhenRequired) {
                                    +"Password is required"
                                } else if (validationPassword.invalidMessage != null) {
                                    +validationPassword.invalidMessage!!
                                }
                            }
                        }
                        hPanel(justifyContent = JustifyContent.Start, gap = 5.px) {
                            fieldWithLabel("Remember me", labelAfter = true) {
                                checkBox(id = it) {
                                    bind(LoginForm::rememberMe)
                                }
                            }
                        }
                        hPanel(justifyContent = JustifyContent.Start, gap = 5.px) {
                            fieldWithLabel("Test") {
                                text(id = it) {
                                    bindCustom(LoginForm::test)
                                }
                            }
                        }
                        hPanel(justifyContent = JustifyContent.Center) {
                            button("Login") {
                                onClick {
                                    if (this@form.validate()) {
                                        println("Login data: ${this@form.getData()}")
                                    }
                                }
                            }
                        }
                    }
                    validator = {
                        if (this[LoginForm::username] == this[LoginForm::password]) {
                            it.copy(isInvalid = true, invalidMessage = "Don't use the same username and password")
                        } else {
                            it
                        }
                    }
                    LaunchedEffect(Unit) {
                        this@form.setData(LoginForm(rememberMe = true, test = ObjectId(Random.nextInt(1, 10))))
                    }
                }

                hr()

                button("navigate to /about") {
                    onEvent<Event>("") {

                    }
                    onCombineClick { }
                    onClick {
                        Router.global.navigate("/about")
                    }
                }

                var home by remember { mutableStateOf("Home") }

                button("change home") {
                    onClick {
                        home = "Home 2"
                        console.log("RoutingModel: ${RoutingModel.global}")
                    }
                }

                hashRouter { ctx ->
                    defaultMeta {
                        author = "Kilua Team"
                    }
                    route("/") {
                        view {
                            p {
                                +"Home"
                                +"RoutingModel: ${RoutingModel.current}"
                            }
                        }
                        meta {
                            description = home
                            keywords = listOf("home", "welcome")
                            view {
                                println("executing meta view for home")
                                title = home
                            }
                        }
                    }
                    route("/article") {
                        meta {
                            titleTemplate = "Article - %s"
                        }
                        route("/head") {
                            meta {
                                title = "Head"
                            }
                            view {
                                p {
                                    +"Head article"
                                }
                            }
                        }
                        int {
                            view { articleId ->
                                p {
                                    +"Article: $articleId"
                                }
                            }
                        }
                        view {
                            p {
                                +"RoutingModel: ${RoutingModel.current}"
                            }
                        }
                        action {
                            console.log("No article ID provided")
                            println(RoutingModel.global.pathList())
                        }
                    }
                    route("/about") {
                        view {
                            p {
                                +"About"
                            }
                        }
                    }
                    route("/admin", "/admin2") {
                        route("/test1") {
                            view {
                                p {
                                    +"Admin test1 page"
                                }
                            }
                            meta {
                                title = "Admin Test1"
                                description = "This is the admin test1 page"
                                keywords = listOf("admin", "test1")
                            }
                        }
                        route("/test2") {
                            route("/xxx") {
                                view {
                                    p {
                                        +"Admin test2 page with xxx"
                                    }
                                }
                            }
                            view {
                                p {
                                    +"Admin test2 page"
                                }
                            }
                        }
                        view {
                            p {
                                +"Admin page"
                            }
                        }
                    }
                    meta {
                        view {
                            description = "Not found page" + ctx.path
                        }
                    }
                    view {
                        p {
                            +"Not found"
                            button("Show remaining path for this page") {
                                onClick {
                                    console.log(ctx.path)
                                    console.log(ctx.parameters.toString())
                                    console.log(ctx.remainingPath)
                                    console.log(Meta.current.toString())
                                }
                            }
                        }
                    }
                }

                val lat = 51.505
                val lng = -0.09
                val positionP = LatLng(lat, lng)
                var zoomP by remember { mutableStateOf(3) }
                maps {
                    width(300.px)
                    height(300.px)
                    configureLeafletMap {
                        setView(positionP, zoomP)
                        println("zoom: $zoomP")
                        val featureGroup = FeatureGroup()
                        featureGroup.addTo(this)
                        val marker = LeafletObjectFactory.marker(positionP)
                        marker.addEventListener("click", {
                            window.open("https://www.openstreetmap.org/?mlat=$lat&mlon=$lng#map=18/$lat/$lng&layers=N")
                        })
                        marker.addTo(featureGroup)
                        DefaultTileLayers.OpenStreetMap.addTo(this)
//                        if (featureGroup.getBounds().isValid()) {
//                            fitBounds(featureGroup.getBounds())
//                        }
                    }
                }
                button("Zoom in") {
                    onClick {
                        zoomP++
                    }
                }
                button("Zoom out") {
                    onClick {
                        zoomP--
                    }
                }

                hr()

                button("A button with an icon", "fas fa-asterisk")
                a("https://google.com", "A link with an icon", "fab fa-google")

                atom("Some label with an icon", "fas fa-plus", separator = " ", iconFirst = false)

                margin(20.px)
                var enabled by remember { mutableStateOf(true) }

                val width: CssSize by animateCssSizeAsState(if (enabled) 100.px else 900.px)
                val color: Color by animateColorAsState(if (enabled) Color.hex(0xff0000) else Color.hex(0x0000ff))
                div {
                    background(color)
                    width(width)
                    height(300.px)
                    border(1.px, BorderStyle.Solid, Color.Blue)
                    +"Test 1"
                    onMouseEnter {
                        enabled = false
                    }
                    onMouseLeave {
                        enabled = true
                    }
                }
                button("Toggle") {
                    onClick {
                        enabled = !enabled
                    }
                }

                hr()

                var itemVisible by remember { mutableStateOf(false) }

                hPanel(gap = 10.px) {
                    button("Show 1") {
                        onClick {
                            itemVisible = true
                        }
                    }
                    button("Hide 1") {
                        onClick {
                            itemVisible = false
                        }
                    }
                    button("Motion") {
                        onClick {
                            animateMini("button", jsObjectOf("rotate" to "360deg"), jsObjectOf("duration" to "2"))
                        }
                    }
                }

                motionAnimatedVisibility(
                    itemVisible,
                    MotionTransition(TransitionType.TranslateLeft),
                ) {
                    div {
                        width(300.px)
                        height(300.px)
                        border(1.px, BorderStyle.Solid, Color.Red)
                        +"Test 2"
                    }
                }

                hr()

                ResponsiveLayout()

                hr()

                Box(Modifier.size(500.px).border(1.px, BorderStyle.Solid, Color.Black)) {
                    Box(Modifier.size(100.px).align(Alignment.TopCenter).background(Color.Red)) {
                        +"TopCenter"
                    }
                    Box(Modifier.size(80.px).align(Alignment.CenterEnd).background(Color.Green)) {
                        +"CenterEnd"
                    }
                    Box(Modifier.fillMaxWidth().height(30.px).align(Alignment.BottomCenter).background(Color.Yellow)) {
                        +"BottomCenter"
                    }
                    Box(Modifier.size(60.px).align(Alignment.Center).background(Color.Silver)) {
                        +"Center"
                    }
                }

                hr()

                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.px, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    div {
                        border(1.px, BorderStyle.Solid, Color.Red)
                        +"Hello World!"
                    }
                    div {
                        border(1.px, BorderStyle.Solid, Color.Red)
                        +"Hello World!"
                    }
                    div {
                        border(1.px, BorderStyle.Solid, Color.Red)
                        +"Hello World!"
                    }
                }

                hr()

                div {
                    var useMod by remember { mutableStateOf(true) }

                    val modifier = Modifier.display(Display.InlineBlock)
                        .id("ala")
                        .onEvent<MouseEvent>("mouseover") {
                            console.log("mouseover")
                        }.title("Tytuł")
                        .className("klasa")
                        .border(1.px, BorderStyle.Solid) then (
                            Modifier.clickable(useMod) {
                                console.log("click")
                            }
//                            if (useMod) {
//                                Modifier.onClick {
//                                    console.log("click")
//                                }
//                            } else {
//                                Modifier.onDblclick {
//                                    console.log("dblclick")
//                                }
//                            }
                            )

                    div {
                        +modifier
                        +"Test"
                    }

                    button("Toggle") {
                        onClick {
                            useMod = !useMod
                        }
                    }
                }

                hr()

                splitPanel {
                    width(300.px)
                    height(300.px)
                    left {
                        width(50.perc)
                        pt("First panel")
                    }
                    right {
                        width(50.perc)
                        pt("Second panel")
                    }
                }
                splitPanel(dir = Dir.Horizontal) {
                    width(300.px)
                    height(300.px)
                    top {
                        height(50.perc)
                        pt("First panel")
                    }
                    bottom {
                        height(50.perc)
                        pt("Second panel")
                    }
                }

                gridPanel(columnGap = 5.px, rowGap = 5.px, justifyItems = JustifyItems.Center) {
                    div {
                        gridRow("1")
                        gridColumn("1")
                        +"Component 1, 1"
                    }
                    div {
                        gridArea("1 / 2")
                        +"Component 1, 2"
                    }
                    div {
                        gridArea("2 / 1")
                        +"Component 2, 1"
                    }
                    div {
                        gridArea("auto")
                        +"Component 2, 2"
                    }
                }

                flexPanel(
                    FlexDirection.Row,
                    FlexWrap.Wrap,
                    JustifyContent.FlexStart,
                    AlignItems.Center,
                    columnGap = 10.px
                ) {
                    div {
                        order(3)
                        flexGrow(2)
                        +"1"
                    }
                    div {
                        order(1)
                        +"2"
                    }
                    div {
                        order(2)
                        +"3"
                    }
                }
                hPanel(gap = 5.px) {
                    div {
                        +"Component 1"
                    }
                    div {
                        +"Component 2"
                    }
                    div {
                        +"Component 3"
                    }
                }
                vPanel(gap = 5.px) {
                    div {
                        +"Component 1"
                    }
                    div {
                        +"Component 2"
                    }
                    div {
                        +"Component 3"
                    }
                }

                div {
                    border(1.px, style = BorderStyle.Solid, color = Color.Red)
                    height(100.px)
                    width(600.px)
                    overflowY(Overflow.Hidden)
                    overflowX(Overflow.Auto)
                    lazyRow({
                        height(100.px)
                        columnGap(2.px)
                        alignItems(AlignItems.Center)
                    }) {
                        items(200) {
                            div {
                                width(30.px)
                                height(30.px)
                                border(1.px, BorderStyle.Solid, Color.Black)
                                +"$it"
                            }
                        }
                    }
                }

                hr()

                var apply by remember { mutableStateOf(true) }

                range {
                    cursor(Cursor.Pointer)
                    style("accent-color", "red")
                    style {
                        style("appearance", "none")
                        background(Color.hex(0xdddddd))
                        pElement("-webkit-slider-thumb") {
                            style("appearance", "none")
                            width(20.px)
                            height(20.px)
                            background(Color.hex(0x8758ff))
                        }
                    }
                }

                range(min = 1, max = 10) {
                    cursor(Cursor.Pointer)
                    flexGrow(1)
                    height(4.px)
                    marginLeft(8.px)
                    marginRight(8.px)
                    style("accent-color", "red")
                    if (apply) {
                        val cls = globalStyle {
                            style("appearance", "none")
                            background(Color.hex(0xdddddd))
                            pElement("-webkit-slider-thumb") {
                                style("appearance", "none")
                                width(20.px)
                                height(20.px)
                                background(Color.hex(0x8758ff))
                            }
                        }
                        className("rrr" % cls)
                    } else {
                        className("rrr")
                    }
                }

                button("Toggle") {
                    onClick {
                        apply = !apply
                    }
                }

                hr()

                val range0 by rangeRef(200, min = 0, max = 200).stateFlow.mapNotNull { it?.toInt() }.collectAsState(0)

                div {
                    style {
                        width(range0.px)
                        height(100.px)
                        border(1.px, style = BorderStyle.Solid, color = Color.Red)
                        pClass(PClass.Hover) {
                            background(Color.Blue)
                        }
                        pElement("-webkit-slider-thumb") {
                            background(Color.Blue)
                        }
                    }
                }

                hr()

                modal(size = ModalSize.ModalSm) {
                    vPanel {
                        bsButton("Close") {
                            onClick {
                                this@modal.hide()
                            }
                        }
                    }
                    LaunchedEffect(Unit) {
                        //    this@modal.show()
                    }
                }

                hr()

                tabPanel {
                    var input by remember { mutableStateOf<String?>(null) }
                    var formData by remember { mutableStateOf<FormData?>(null) }

                    tab("Tab 1") {
                        text(input) {
                            onChange {
                                input = this.value
                            }
                        }
                    }
                    tab("Tab 2") {
                        val f = formRef<FormData> {
                            text {
                                bind(FormData::name)
                            }
                            text(required = true) {
                                bind(FormData::lname)
                            }
                            richDate(required = true) {
                                bind(FormData::date)
                            }
                            DisposableEffect(Unit) {
                                formData?.let { setData(it) }
                                val job = stateFlow.onEach {
                                    formData = it
                                }.launchIn(KiluaScope)
                                onDispose {
                                    job.cancel()
                                }
                            }
                        }
                        button("Validate") {
                            onClick {
                                console.log(f.validate().toString())
                                console.log(f.getData().toString())
                            }
                        }
                    }
                }


                hr()

                var option by remember { mutableStateOf("1") }

                select(listOf("1" to "one", "2" to "Two"), option)

                button("test select") {
                    onClick {
                        option = "2"
                    }
                }

                br()

                val size by rangeRef(200, min = 0, max = 200).collectAsState()

                br()

                svg(viewBox = "0 0 20 20") {
                    width(size?.toInt()?.px)
                    height(size?.toInt()?.px)
                    fill("currentColor")
                    className("mr-2")
                    path("M10 5a1 1 0 0 1 1 1v3h3a1 1 0 1 1 0 2h-3v3a1 1 0 1 1-2 0v-3H6a1 1 0 1 1 0-2h3V6a1 1 0 0 1 1-1Z")
                }

                hr()

                var count by remember { mutableStateOf(0) }
                tag("h1") {
                    println("recomposing div xx")
                    +"Hello World! $count"
                    if (count == 1) {
                        background(Color.Blue)
                        attribute("x-test", "test 1")
                        title("ala")
                        className("ala")
                    }
                    if (count == 2) {
                        background(Color.Aqua)
                        attribute("x-test", "test 2")
                        title("ala2")
                        className("ma")
                    }
                    if (count == 4) {
                        background(Color.Green)
                    }
                    onClick {
                        cast<Tag<HTMLElement>>().title = "test"
                    }
                    // for count > 1 background color should be undefined again!
                }
                tag("h1") {
                    +"Hello World 2! $count"
                    if (count == 1) {
                        background(Color.Yellow)
                        attribute("x-test", "test 1")
                        title("ala")
                        className("ala")
                    }
                    if (count == 2) {
                        background(Color.Orange)
                        attribute("x-test", "test 2")
                        title("ala2")
                        className("ma")
                    }
                    if (count == 3) {
                        background(Color.Fuchsia)
                    }
                }
                button("Button") {
                    onClick { count++ }
                }

                h1("text-blue-500") {
                    +i18n.tr("Hello world!")
                }

                button("Change language") {
                    onClick {
                        LocaleManager.setCurrentLocale(SimpleLocale("es"))
                    }
                }

                val progress = Progress(ProgressOptions(height = 10, color = Color.Lightgreen))

                button("Progress start") {
                    onClick {
                        progress.promise(Promise { resolve, reject ->
                            setTimeout({
                                resolve(obj())
                            }, 3000)
                        })
                    }
                }

                button("Progress end") {
                    onClick {
                        progress.end()
                    }
                }

                hr()

                val personList = remember {
                    mutableStateListOf(
                        Person("John", 30, "New York"),
                        Person("Alice", 25, "Los Angeles"),
                        Person("John", 30, "New York"),
                        Person("Alice", 25, "Los Angeles"),
                        Person("Alice", 25, "Los Angeles"),
                        Person("Alice", 25, "Los Angeles")
                    )
                }

                var heighttab by remember { mutableStateOf(300) }

                console.log("recompose before tabulator")

                tabulator<Person>(
                    personList,
                    options = TabulatorOptions(
                        height = "${heighttab}px",
                        layout = Layout.FitColumns,
                        responsiveLayout = ResponsiveLayout.Collapse,
                        responsiveLayoutCollapseStartOpen = false,
                        columns = listOf(
                            ColumnDefinition(
                                "",
                                "collapse",
                                formatter = Formatter.ResponsiveCollapseAuto
                            ),
                            ColumnDefinition("Name", "name", titleFormatterComponentFunction = { _, _ ->
                                span {
                                    +"Kilua Name"
                                }
                            }, minWidth = 300),
                            ColumnDefinition("Age", "age", formatterFunction = { cell, params, onRendered ->
                                cell.getValue()!!
                            }, editorComponentFunction = { cell, onRendered, success, cancel, data ->
                                text(data.age.toString()) {
                                    onChange {
                                        success(this.value?.toIntOrNull()?.toJsInt())
                                    }
                                    onBlur {
                                        cancel(null)
                                    }
                                    LaunchedEffect(Unit) {
                                        focus()
                                    }
                                }
                            }, minWidth = 300),
                            ColumnDefinition(
                                "City", "city",
                                formatterComponentFunction = { _, _, data ->
                                    var x by remember { mutableStateOf(0) }
                                    div {
                                        +(data.city + " " + x)
                                        onClick {
                                            x++
                                        }
                                    }
                                }, minWidth = 300
                            ),
                            ColumnDefinition("", "actions", responsive = 0, headerColumnsMenu = true)
                        ), pagination = true, paginationMode = PaginationMode.Local, paginationSize = 10
                    ), types = setOf(TableType.TableBordered, TableType.TableStriped, TableType.TableSm)
                )

                button("Add row") {
                    onClick {
                        personList.add(Person("Mike", 70, "Chicago"))

                    }
                }
                button("Change height") {
                    onClick {
                        heighttab += 10
                    }
                }

                hr()

                val dynamicList = remember {
                    mutableStateListOf(
                        jsObjectOf("id" to 1, "name" to "John", "age" to 30, "city" to "New York"),
                        jsObjectOf("id" to 2, "name" to "Alice", "age" to 25, "city" to "Los Angeles"),
                        jsObjectOf("id" to 3, "name" to "Mike", "age" to 70, "city" to "Chicago"),
                    )
                }

                tabulator(
                    dynamicList, options = TabulatorOptions(
                        height = "300px",
                        layout = Layout.FitColumns,
                        columns = listOf(
                            ColumnDefinition("Name", "name"),
                            ColumnDefinition("Age", "age", formatterFunction = { cell, params, onRendered ->
                                cell.getValue()!!
                            }),
                            ColumnDefinition("City", "city", formatterComponentFunction = { cell, params, data ->
                                div {
                                    +data.jsGet("city").toString()
                                }
                            }, editorComponentFunction = { cell, onRendered, success, cancel, data ->
                                text(data.jsGet("city").toString()) {
                                    onChange {
                                        success(this.value?.toJsString())
                                    }
                                    onBlur {
                                        cancel(null)
                                    }
                                    LaunchedEffect(Unit) {
                                        focus()
                                    }
                                }
                            })
                        ), pagination = true, paginationMode = PaginationMode.Local, paginationSize = 10
                    ), types = setOf(TableType.TableBorderless, TableType.TableSm)
                )

                button("Add row") {
                    onClick {
                        dynamicList.add(jsObjectOf("id" to 4, "name" to "Tom", "age" to 40, "city" to "Boston"))
                    }
                }

                hr()

                val nativeList = remember {
                    jsArrayOf(
                        jsObjectOf("id" to 1, "name" to "John", "age" to 30, "city" to "New York"),
                        jsObjectOf("id" to 2, "name" to "Alice", "age" to 25, "city" to "Los Angeles"),
                        jsObjectOf("id" to 3, "name" to "Mike", "age" to 70, "city" to "Chicago"),
                    )
                }

                tabulator(
                    options = TabulatorOptions(
                        height = "300px",
                        data = nativeList,
                        layout = Layout.FitColumns,
                        columns = listOf(
                            ColumnDefinition("Name", "name"),
                            ColumnDefinition("Age", "age", formatterFunction = { cell, params, onRendered ->
                                cell.getValue()!!
                            }),
                            ColumnDefinition("City", "city", formatterComponentFunction = { cell, params, data ->
                                div {
                                    +data.jsGet("city").toString()
                                }
                            }, editorComponentFunction = { cell, onRendered, success, cancel, data ->
                                text(data.jsGet("city").toString()) {
                                    onChange {
                                        success(this.value?.toJsString())
                                    }
                                    onBlur {
                                        cancel(null)
                                    }
                                    LaunchedEffect(Unit) {
                                        focus()
                                    }
                                }
                            }),
                        ), pagination = true, paginationMode = PaginationMode.Local, paginationSize = 10
                    ), types = setOf(TableType.TableHover, TableType.TableSm)
                )

                vPanel {
                    text {
                        maskOptions = ImaskOptions(pattern = PatternMask("000-000-000"))
                    }
                    text {
                        maskOptions = ImaskOptions(pattern = PatternMask("000-000-000", eager = true))
                    }
                    text {
                        maskOptions = ImaskOptions(pattern = PatternMask("000-000-000", lazy = false, eager = true))
                    }
                    text {
                        maskOptions = ImaskOptions(
                            pattern = PatternMask(
                                "{Ple\\ase fill ye\\ar 19}YY{, month }MM{ \\and v\\alue }VL",
                                lazy = false,
                                blocks = mapOf(
                                    "YY" to ImaskOptions(pattern = PatternMask("00")),
                                    "MM" to ImaskOptions(range = RangeMask(1, 12)),
                                    "VL" to ImaskOptions(enum = EnumMask(listOf("TV", "HD", "VR")))
                                )
                            )
                        )
                        onChange {
                            console.log(this.value)
                        }
                    }
                    text {
                        maskOptions =
                            ImaskOptions(range = RangeMask(0, 100, maxLength = 3, autofix = MaskAutofix.Pad))
                    }
                    numeric(123.44, placeholder = "Enter a number") {
                        maskOptions = ImaskOptions(
                            number = NumberMask(
                                scale = 2,
                                padFractionalZeros = true,
                                normalizeZeros = true,
                                min = -10000,
                                max = 10000,
                            )
                        )
                        onInput {
                            console.log(this.value?.toString())
                        }
                    }
                    text {
                        maskOptions = ImaskOptions(regExp = RegExp("^[0-9]*$"))
                    }
                    text {
                        maskOptions = ImaskOptions(function = { it.startsWith("1") })
                        onInput {
                            console.log(this.value)
                        }
                    }
                    imaskNumeric(123.45) {
                        onInput {
                            console.log(this.value.toString())
                        }
                    }
                    text {
                        maskOptions = ImaskOptions(pattern = PatternMask("00{-}000", lazy = false, eager = true))
                    }
                }

                hr()

                var ttdis by remember { mutableStateOf(false) }
                var ttid by remember { mutableStateOf("a") }

                val tt = tomTypeaheadRef(
                    listOf("Alaska", "California", "Nevada", "Oregon", "Washington"),
                    placeholder = "enter",
                    disabled = ttdis,
                    id = ttid
                )

                button("get value") {
                    onClick {
                        console.log(tt.value)
                    }
                }
                button("set value") {
                    onClick {
                        tt.value = "New York"
                    }
                }
                button("set null") {
                    onClick {
                        tt.value = null
                    }
                }
                button("toggle disabled") {
                    onClick {
                        ttdis = !ttdis
                    }
                }
                button("change id") {
                    onClick {
                        ttid += "a"
                    }
                }

                hr()

                val restClient2 = RestClient()

                console.log("recomposing before tom select")

                tomSelect(emptyOption = true) {
                    emptyOption(true)
                    tsCallbacks(
                        TomSelectCallbacks(
                            load = { query, callback ->
                                promise {
                                    val result = try {
                                        restClient2.callDynamic("https://api.github.com/search/repositories") {
                                            data = jsObjectOf("q" to query)
                                            resultTransform = { it?.jsGet("items") }
                                        }
                                    } catch (e: RemoteRequestException) {
                                        console.log(e.toString())
                                        null
                                    }
                                    result?.let { items: JsAny ->
                                        callback(items.unsafeCast<JsArray<JsAny>>().toList().map { item ->
                                            jsObjectOf(
                                                "value" to item.jsGet("id")!!,
                                                "text" to item.jsGet("name")!!,
                                                "subtext" to item.jsGet("owner")?.jsGet("login")!!
                                            )
                                        }.toJsArray())
                                    } ?: callback(jsArrayOf())
                                    obj()
                                }
                            },
                            shouldLoad = { it.length >= 3 }
                        ))
                    tsRenders(TomSelectRenders(option = { item, escape ->
                        val subtext: String? = item.jsGet("subtext")?.toString()
                        """
                                        <div>
                                            <span class="title">${escape(item.jsGet("text").toString())}</span>
                                            <small>${subtext?.let { "(" + escape(it) + ")" } ?: ""}</small>
                                        </div>
                                    """.trimIndent()
                    }))
                    onChange {
                        console.log(this.value)
                    }
                }

                hr()

                var tsvalue by remember { mutableStateOf<String?>("dog") }
                var multi by remember { mutableStateOf(true) }
                var tsdis by remember { mutableStateOf(false) }

                label("test-tom-select") {
                    +"A Field"
                }

                val tselect = tomSelectRef(
                    listOf("cat" to "Cat", "dog" to "Dog", "mouse" to "Mouse"),
                    value = tsvalue,
                    placeholder = "Select an option",
                    emptyOption = true,
                    multiple = multi,
                    disabled = tsdis,
                    id = "test-tom-select"
                ) {
                    LaunchedEffect(Unit) {
                        stateFlow.onEach {
                            console.log(it)
                        }.launchIn(KiluaScope)
                    }
                }

                button("Get value") {
                    onClick {
                        console.log(tselect.value)
                        console.log(tselect.selectedLabel)
                    }
                }
                button("Set value") {
                    onClick {
                        tselect.value = "mouse"
                    }
                }
                button("Set null") {
                    onClick {
                        tselect.value = null
                    }
                }
                button("Toggle disabled") {
                    onClick {
                        tsdis = !tsdis
                    }
                }

                hr()

                val letterIndexes = List(26) { it }

                val randomElements = remember { mutableStateListOf<UInt>() }

                div {
                    border(1.px, style = BorderStyle.Solid, color = Color.Red)
                    height(600.px)
                    overflow(Overflow.Auto)

                    lazyColumn {
                        item {
                            textNode("First element")
                        }

                        items(200) {
                            textNode("Element #$it")
                        }

                        item {
                            button("Generate random elements") {
                                onClick {
                                    Snapshot.withMutableSnapshot {
                                        randomElements.clear()
                                        repeat(Random.nextInt(10..100)) {
                                            randomElements.add(Random.nextUInt())
                                        }
                                    }
                                }
                            }
                        }

                        items(randomElements) {
                            textNode("Random value: $it")
                        }

                        items(letterIndexes) {
                            val lowerCase = it + 'a'.code

                            textNode("Letter ${lowerCase.toChar()}")
                        }

                        items(letterIndexes) {
                            val upperCase = it + 'A'.code

                            textNode("Letter ${upperCase.toChar()}")
                        }
                    }
                }

                var dtInline by remember { mutableStateOf(false) }

                val rd = richDateTimeRef(
                    now(),
                    placeholder = "Podaj datę",
                    inline = dtInline,
                    id = "test",
                    inputClassName = "form-control-lg"
                ) {
                    keyboardNavigation(false)
                    name("data")
                    required(true)
                    autocomplete(Autocomplete.Off)
                    onChange {
                        console.log(this.getValueAsString())
                    }
                }

                richDate(today()) {
                    onChange {
                        console.log(this.getValueAsString())
                    }
                }

                richTime(now().time) {
                    hourCycle(HourCycle.H11)
                    onChange {
                        console.log(this.getValueAsString())
                    }
                }

                button("Toggle inline") {
                    onClick {
                        dtInline = !dtInline
                    }
                }

                button("Get value") {
                    onClick {
                        console.log(rd.value.toString())
                    }
                }

                button("Set value") {
                    onClick {
                        rd.value = now()
                    }
                }

                button("Set null") {
                    onClick {
                        rd.value = null
                    }
                }

                button("toggle") {
                    onClick {
                        rd.tempusDominusInstance?.toggle()
                        it.stopPropagation()
                    }
                }

                hr()

                button("Show toastify msg") {
                    onClick {
                        dev.kilua.toastify.toast(
                            "Test toastify",
                            type = ToastType.Danger,
                            duration = 30.seconds,
                            close = true
                        )
                    }
                }

                button("Test REST Client") {
                    onClick {
                        promise {
                            RestClient().callDynamic<Query>(
                                "https://api.github.com/search/repositories",
                                Query("kvision")
                            ) {
                                this.resultTransform = { it!!.jsGet("total_count") }
                            }
                        }.then {
                            console.log(it)
                            obj()
                        }
                    }
                }

                hr()

                dropDown("A dropdown", "fas fa-search", style = ButtonStyle.BtnDanger, arrowVisible = false) {
                    li {
                        a("#", "Link 1", className = "dropdown-item")
                    }
                    li {
                        a("#", "Link 2", className = "dropdown-item")
                    }
                    li {
                        dropDown("An inner dropdown", innerDropDown = true) {
                            li {
                                a("#", "Link 3", className = "dropdown-item")
                            }
                            li {
                                a("#", "Link 4", className = "dropdown-item")
                            }
                        }
                    }
                }

                hr()

                var positionIndex by remember { mutableStateOf(0) }
                val toastPosition = ToastPosition.entries[positionIndex % ToastPosition.entries.size]

                button("Show toast") {
                    onClick {
                        toast("Test toast", position = toastPosition, color = BsColor.TextBgSuccess)
                    }
                }
                button("test toast") {
                    onClick {
                        positionIndex++
                    }
                }

                hr()

                themeSwitcher(style = ButtonStyle.BtnSuccess, round = true)
                themeSwitcher(autoIcon = "bi bi-circle-half", darkIcon = "bi bi-moon", lightIcon = "bi bi-sun")

                hr()

                val off =
                    offcanvasRef(
                        "Test offcanvas",
                        OffPlacement.OffcanvasEnd,
                        closeButton = true,
                        bodyScrolling = true,
                        backdrop = true,
                        escape = true
                    ) {
                        pt(
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam quis risus eget urna mollis ornare vel eu leo. " +
                                    "Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nullam id dolor id nibh " +
                                    "ultricies vehicula ut id elit. Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Praesent commodo " +
                                    "cursus magna, vel scelerisque nisl consectetur et. Fusce dapibus, tellus ac cursus commodo, tortor mauris " +
                                    "condimentum nibh, ut fermentum massa justo sit amet risus. Duis mollis, est non commodo luctus, nisi erat " +
                                    "porttitor ligula, eget lacinia odio sem nec elit. Aenean lacinia bibendum nulla sed consectetur. Praesent " +
                                    "commodo cursus magna, vel scelerisque nisl consectetur et. Donec sed odio dui. Donec ullamcorper nulla non " +
                                    "metus auctor fringilla. Cras mattis consectetur purus sit amet fermentum. Cras justo odio, dapibus ac " +
                                    "facilisis in, egestas eget quam. Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Praesent " +
                                    "commodo cursus magna, vel scelerisque nisl consectetur et. Cras mattis consectetur purus sit amet fermentum. " +
                                    "Cras justo odio, dapibus ac facilisis in, egestas eget quam. Morbi leo risus, porta ac consectetur ac, " +
                                    "vestibulum at eros. Praesent commodo cursus magna, vel scelerisque nisl consectetur et. Cras mattis " +
                                    "consectetur purus sit amet fermentum. Cras justo odio, dapibus ac facilisis in, egestas eget quam. Morbi " +
                                    "leo risus, porta ac consectetur ac, vestibulum at eros. Praesent commodo cursus magna, vel scelerisque " +
                                    "nisl consectetur et. Cras mattis consectetur purus sit amet fermentum. Cras justo odio, dapibus ac " +
                                    "facilisis in, egestas eget quam. Mor"
                        )
                    }

                var tooltip by remember { mutableStateOf("Test") }

                val bsButton = bsButtonRef("toggle offcanvas") {
                    onClick {
                        off.toggle()
                    }
                    if (tooltip == "TestX") {
                        tooltip(tooltip, placement = Placement.Bottom, delay = 2.seconds)
                    } else {
                        popover(tooltip, tooltip, placement = Placement.Right, triggers = listOf(Trigger.Hover))
                    }
                }

                bsButton("change tooltip") {
                    onClick {
                        tooltip += "X"
                    }
                }

                bsButton("toggle tooltip") {
                    onClick {
                        bsButton.toggleTooltip()
                    }
                }


                bsButton("enable tooltip") {
                    onClick {
                        bsButton.enableTooltip()
                    }
                }

                bsButton("disable tooltip") {
                    onClick {
                        bsButton.disableTooltip()
                    }
                }

                hr()

                carousel(hideIndicators = true, autoPlay = true) {
                    item("First slide", "First slide label") {
                        div("d-block w-100") {
                            height(200.px)
                            background(Color.Red)
                            pt("Nulla vitae elit libero, a pharetra augue mollis interdum.")
                        }
                    }
                    item("Second slide", "Second slide label") {
                        div("d-block w-100") {
                            height(200.px)
                            background(Color.Green)
                            pt("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
                        }
                    }
                    item("Third slide", "Third slide label") {
                        div("d-block w-100") {
                            height(200.px)
                            background(Color.Blue)
                            pt("Praesent commodo cursus magna, vel scelerisque nisl consectetur.")
                        }
                    }
                }

                hr()

                var accName by remember { mutableStateOf("Test") }

                accordion(true) {
                    item("Search item", "fas fa-search") {
                        pt("First item content", "mb-0")
                    }
                    if (accName == "Test2") {
                        item(accName, "fas fa-times") {
                            pt(accName, "mb-0")
                        }
                    } else {
                        item(accName, "fas fa-times") {
                            h2t(accName, "mb-0")
                        }
                    }
                    item("Third item", "fab fa-chrome") {
                        pt("Third item content", "mb-0")
                    }
                }

                button("modify accordion", "bi-star", className = "btn btn-primary") {
                    onClick {
                        accName += "2"
                    }
                }

                hr()

                fieldWithLabel("Floating label", "form-label", true, groupClassName = "form-floating") {
                    text("Mark", placeholder = "placeholder", className = "form-control form-control-sm")
                }

                fieldWithLabel("A switch", "form-check-label", true, groupClassName = "form-check form-switch") {
                    checkBox(true, className = "form-check-input", id = it) {
                        role("switch")
                    }
                }

                hr()
                bsButton("Test", "fas fa-check", size = ButtonSize.BtnLg, style = ButtonStyle.BtnDanger) {
                    onClick {
                        console.log("test")
                    }
                }

                hr()

                var modalCaption by remember { mutableStateOf("Test") }

                if (modalCaption == "Test2") {
                    globalStyle(modalCaption) {
                        margin = 20.px
                    }
                }

                val modal = modalRef(
                    modalCaption,
                    size = ModalSize.ModalXl,
                    fullscreenMode = FullscreenMode.ModalFullscreenMdDown,
                    centered = false,
                    scrollable = true,
                    escape = true
                ) {
                    pt(modalCaption)
                    footer {
                        buttonRef("OK").onClick {
                            this@modalRef.hide()
                        }
                        buttonRef("Test").onClick {
                            modalCaption += "2"
                            alert("Test alert", "Test <br>content", rich = true)
                        }
                    }
                }
                button("show modal") {
                    onClick {
                        modal.show()
                    }
                }
                button("modal class") {
                    onClick {
                        confirm(modalCaption, "Test content", cancelVisible = true, noCallback = {
                            console.log("no callback")
                        }) {
                            console.log("yes callback")
                        }
                    }
                }
                button("Caption") {
                    onClick {
                        modalCaption += "2"
                    }
                }

                hr()

                var splitState by remember { mutableStateOf(0) }

                splitPanel {
                    width(500.px)
                    height(300.px)
                    margin(30.px)
                    if (splitState != 1) {
                        left {
                            width(20.perc)
                            pt("left$splitState")
                        }
                        right {
                            pt("right$splitState")
                        }
                    } else {
                        left {
                            width(50.perc)
                            pt("top$splitState")
                        }
                        right {
                            width(50.perc)
                            pt("bottom$splitState")
                        }
                    }
                }

                button("test split") {
                    onClick {
                        splitState += 1
                    }
                }

                hr()

                var draggableTabs by remember { mutableStateOf(false) }
                var selectedTab by remember { mutableStateOf(0) }

                var tabName by remember { mutableStateOf("Test") }

                tabPanel(activeIndex = selectedTab, tabPosition = TabPosition.Top, draggableTabs = draggableTabs) {
                    console.log("recompose tabPanel 1 (selectedTab: $selectedTab)")
                    margin(20.px)
                    tab("Test1", "fas fa-search", closable = true) {
                        pt("Test1")
                    }
                    if (tabName == "Test2") {
                        tab(tabName, "fas fa-times", closable = true) {
                            pt(tabName)
                        }
                    } else {
                        tab(tabName, "fas fa-times", closable = true) {
                            h2t(tabName)
                        }
                    }
                    tab("Test3", "fab fa-chrome", closable = true) {
                        pt("Test3")
                    }
                    onEvent<CustomEvent<*>>("closeTab") {
                    }
                }

                a("https://www.google.com", "Google", "bi-google", className = "btn btn-primary")

                br()

                button("toggle tabPanel", "bi-star", className = "btn btn-primary") {
                    onClick {
                        tabName += "2"
                    }
                }

                hr()

                val tabs = remember { mutableStateListOf("First tab", "Second tab") }

                tabPanel(activeIndex = 0) {
                    margin(30.px)
                    console.log("recompose tabPanel")
                    tabs.forEach { tab ->
                        console.log("generate tab: $tab")
                        key(tab) {
                            tab(tab, null, closable = true) {
                                pt(tab)
                            }
                        }
                    }
                    onEvent<CustomEvent<*>>("closeTab") {
                        tabs.removeAt(it.detail.toString().toIntOrNull() ?: 0)
                    }
                }

                button("add tab") {
                    onClick {
                        tabs.add("New tab")
                    }
                }

                button("remove tab") {
                    onClick {
                        tabs.removeAt(1)
                    }
                }

                hr()

                var disab by remember { mutableStateOf(true) }

                val trix = richTextRef("ala ma kota", disabled = disab, placeholder = "wprowadź dane") {
                    onChange {
                        console.log(this.value)
                    }
                }
                button("get trix") {
                    onClick {
                        console.log(trix.value)
                    }
                }
                button("set trix") {
                    onClick {
                        trix.value = "<strong>bold text</strong>"
                    }
                }
                button("clear trix") {
                    onClick {
                        trix.value = null
                    }
                }
                button("hide trix") {
                    onClick {
                        trix.visible = !trix.visible
                    }
                }
                button("disable trix") {
                    onClick {
                        disab = !disab
                    }
                }

                hr()

                val i by rangeRef(0, 1, 255).collectAsState()

                val className = globalStyle(".test") {
                    console.log("recompose 1")
                    background(Color.rgb(i?.toInt() ?: 0, 0, 0))
                    style("h1", PClass.Hover) {
                        color(Color.Green)
                    }
                    style("h1") {
                        style("div") {
                            color(Color.Blue)
                        }
                    }
                    style("input", PClass.Focus) {
                        border(1.px, BorderStyle.Solid, Color.Red)
                    }
                }

                div {
                    h1t("Ala ma kota") {
                        div {
                            +"test"
                        }
                    }
                    margin(20.px)
                    form(className = "row g-3 needs-validation") {
                        val validation by validationStateFlow.collectAsState()

                        if (validation.isInvalid) {
                            pt("form is invalid: ${validation.invalidMessage}")
                            validation.fieldsValidations.forEach { (key, value) ->
                                if (value.isEmptyWhenRequired) pt("field is empty: $key")
                                if (value.isInvalid) pt("field is invalid: $key - ${value.invalidMessage}")
                            }
                        } else if (validation.validMessage != null) {
                            pt("form is valid: ${validation.validMessage}")
                        }

                        fieldWithLabel("First name", "form-label", groupClassName = "col-md-4") {
                            textRef("Mark", required = true, className = "form-control").bind("firstName").also {
                                div("valid-feedback") {
                                    +"Looks good!"
                                }
                            }
                        }
                        fieldWithLabel("Last name", "form-label", groupClassName = "col-md-4") {
                            textRef("Otto", required = true, className = "form-control").bind("lastName").also {
                                div("valid-feedback") {
                                    +"Looks good!"
                                }
                            }
                        }
                        fieldWithLabel(
                            "Username", "form-label", groupClassName = "col-md-4",
                            wrapperClassName = "input-group has-validation"
                        ) {
                            span("input-group-text") {
                                id("inputGroupPrepend")
                                +"@"
                            }
                            val invalidClass = if (validation["username"]?.isInvalid == true) "is-invalid" else null
                            textRef(required = true, className = "form-control" % invalidClass) {
                                ariaDescribedby("inputGroupPrepend")
                            }.bindWithValidationMessage("username") { text ->
                                val result = text.value == null || text.value!!.length >= 10
                                val message = if (!result) "Username must be at least 10 characters long." else null
                                result to message
                            }.also {
                                div("invalid-feedback") {
                                    +(validation["username"]?.invalidMessage ?: "Please choose a username.")
                                }
                            }
                        }
                        fieldWithLabel("City", "form-label", groupClassName = "col-md-6") {
                            textRef(required = true, className = "form-control").bind("city").also {
                                div("invalid-feedback") {
                                    +"Please provide a valid city."
                                }
                            }
                        }
                        fieldWithLabel("State", "form-label", groupClassName = "col-md-3") {
                            tomTypeaheadRef(
                                listOf("Alaska", "California"),
                                placeholder = "Choose...",
                                id = it,
                                required = true
                            ).bind("state")
                            div("invalid-feedback") {
                                +"Please select a valid state."
                            }
                            /*                            tomSelect(
                                                            listOfPairs("Alaska", "California"),
                                                            emptyOption = true,
                                                            placeholder = "Choose...",
                                                            id = it,
                                                            multiple = true,
                                                            className = "form-select",
                                                            required = true
                                                        ).bind("state")
                                                        div("invalid-feedback") {
                                                            +"Please select a valid state."
                                                        }*/

                            /*richDate(
                                placeholder = "Choose...",
                                id = it,
                                required = true
                            ).bind("state")
                            div("invalid-feedback") {
                                +"Please select a valid state."
                            }*/

                            /*select(listOfPairs("Alaska"), className = "form-select", placeholder = "Choose...")
                                .bind("state").also {
                                    div("invalid-feedback") {
                                        +"Please select a valid state."
                                    }
                                }*/
                        }
                        fieldWithLabel("Zip", "form-label", groupClassName = "col-md-3") {
                            textRef(required = true, className = "form-control").bind("zip").also {
                                div("invalid-feedback") {
                                    +"Please provide a valid zip."
                                }
                            }
                        }
                        div("col-12") {
                            div("form-check") {
                                fieldWithLabel(
                                    "Agree to terms and conditions",
                                    "form-check-label",
                                    labelAfter = true
                                ) {
                                    checkBoxRef(className = "form-check-input", required = true).bind("agree")
                                }
                                div("invalid-feedback") {
                                    +"You must agree before submitting."
                                }
                            }
                        }
                        div("col-12") {
                            button("Submit form", className = "btn btn-primary") {
                                onClick {
                                    val x = this@form.validate()
                                    console.log(x.toString())
                                    this@form.className = "row g-3 was-validated"
                                    val data = this@form.getData()
                                    console.log(data.toString())
                                }
                            }
                        }
                        //                    stateFlow.onEach {
                        //                        this.validate()
                        //                    }.launchIn(KiluaScope)
                    }

                }
            }

            console.log("recomposing")
            val x = tag("address", "address3") {
                +"address23"
                //                setStyle("color", "red")
            }

            var size by remember { mutableStateOf(1) }
            var evenSize by remember { mutableStateOf(1) }
            var oddSize by remember { mutableStateOf(1) }
            var tn by remember { mutableStateOf("address") }
            var list by remember { mutableStateOf(listOf("cat", "dog", "mouse")) }
            var type by remember { mutableStateOf(ButtonType.Button) }

            div {
                for (name in list) {
                    //                    key(name) {
                    div {
                        +name
                        button {
                            +"click"
                            DisposableEffect("button") {
                                val f = { _: Event ->
                                    console.log("click $name of ${list.size}")
                                }
                                element.addEventListener(EventType<Event>("click"), f)
                                onDispose {
                                    element.removeEventListener(EventType<Event>("click"), f)
                                }
                            }
                        }
                        //                      }
                    }
                }
            }
            val xb = buttonRef("add $size") {
                onClick {
                    list = list + "test"
                }
            }
            button {
                +"remove"
                onClick {
                    list = list.filterIndexed { index, s -> index != 1 }
                    xb.textContent = "changed label"
                }
            }

            tag(tn) {
                +"address2 $size"
                //    id = "test"
                //    title = "Some title"
                //role = "button"
                //   tabindex = 5
                //    draggable = true
                //   setAttribute("aria-label", "Ala ma kota")
            }
            val x2 = text("Ala ma kota")
            button(type = type) {
                +"test span"
                onClick {
                    cast<Button>().disabled = !(disabled ?: false)
                    setTimeout({
                        cast<Button>().disabled = !(disabled ?: false)
                    }, 1000)
                }
            }

            if (size % 2 == 0) {
                div {
                    +"even $evenSize"
                    button {
                        +"button even$evenSize"
                        onClick {
                            evenSize++
                        }
                    }
                }
            } else {
                div {
                    +"odd $oddSize"
                    button {
                        +"button odd$oddSize"
                        onClick {
                            oddSize++
                        }
                    }
                }
            }

            lateinit var divB: Div
            div {
                +"$size"
                divB = divRef {
                    +"b"
                    div {
                        +"c"
                        button(disabled = (size % 4 == 0)) {
                            +"button1"
                            onClick {
                                divB.textContent = "test"
                                size++
                            }
                        }
                        if (size % 2 == 0) {
                            button {
                                +"button2"
                                onClick {
                                    size++
                                }
                                DisposableEffect("button2") {
                                    val f = { _: Event ->
                                        console.log("button2 click")
                                    }
                                    element.addEventListener(EventType<Event>("click"), f)
                                    onDispose {
                                        element.removeEventListener(EventType<Event>("click"), f)
                                    }
                                }
                            }
                        }
                        for (i in 0 until size) {
                            div {
                                +"$i"
                            }
                        }
                    }
                }
                DisposableEffect("code") {
                    element.firstChild?.unsafeCast<Text>()?.data = "ala ma kota"
                    onDispose { }
                }
            }

        }
    }
}

@Composable
private fun IComponent.ResponsiveLayout() {
    val windowAdaptiveInfo = currentWindowSizeClass()
    val breakpoint by rememberBreakpoint()
    val tailwindcssBreakpoint by rememberTailwindcssBreakpoint()
    val orientation by rememberOrientation()

    div {
        +Modifier
            .width(
                when (windowAdaptiveInfo.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> 50.px
                    WindowWidthSizeClass.Medium -> 100.px
                    WindowWidthSizeClass.Expanded -> 200.px
                }
            )
            .height(
                when (windowAdaptiveInfo.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> 50.px
                    WindowWidthSizeClass.Medium -> 100.px
                    WindowWidthSizeClass.Expanded -> 200.px
                }
            )
            .background(Color.Red)
    }
    div {
        +Modifier
            .width(
                when (breakpoint) {
                    Breakpoint.Mobile -> 50.px
                    Breakpoint.SmallTablet -> 100.px
                    Breakpoint.Tablet -> 150.px
                    Breakpoint.Laptop -> 200.px
                    Breakpoint.Desktop -> 300.px
                }
            )
            .height(
                when (breakpoint) {
                    Breakpoint.Mobile -> 50.px
                    Breakpoint.SmallTablet -> 100.px
                    Breakpoint.Tablet -> 150.px
                    Breakpoint.Laptop -> 200.px
                    Breakpoint.Desktop -> 300.px
                }
            )
            .background(Color.Green)
    }

    div {
        +Modifier
            .width(
                when (tailwindcssBreakpoint) {
                    TailwindcssBreakpoint.DEFAULT -> 50.px
                    TailwindcssBreakpoint.SM -> 100.px
                    TailwindcssBreakpoint.MD -> 150.px
                    TailwindcssBreakpoint.LG -> 200.px
                    TailwindcssBreakpoint.XL -> 250.px
                    TailwindcssBreakpoint.XL2 -> 300.px
                }
            )
            .height(
                when (tailwindcssBreakpoint) {
                    TailwindcssBreakpoint.DEFAULT -> 50.px
                    TailwindcssBreakpoint.SM -> 100.px
                    TailwindcssBreakpoint.MD -> 150.px
                    TailwindcssBreakpoint.LG -> 200.px
                    TailwindcssBreakpoint.XL -> 250.px
                    TailwindcssBreakpoint.XL2 -> 300.px
                }
            )
            .background(Color.Blue)
    }

    divt(orientation.toString())
}
