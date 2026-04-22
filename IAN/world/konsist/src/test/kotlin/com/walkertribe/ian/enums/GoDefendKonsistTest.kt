package com.walkertribe.ian.enums

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.provider.hasReceiverTypeOf
import com.lemonappdev.konsist.api.ext.provider.hasReturnTypeOf
import com.lemonappdev.konsist.api.verify.assertTrue
import io.kotest.core.spec.style.DescribeSpec

class GoDefendKonsistTest :
    DescribeSpec({
        val enums = "com.walkertribe.ian.enums"
        val enumsScope = Konsist.scopeFromPackage(enums, "IAN/world", "main")
        val goDefendName = "GoDefend"

        describe(goDefendName) {
            val goDefendFunc = enumsScope.functions()

            it("No classes, interfaces or objects") {
                goDefendFunc.assertTrue { func ->
                    func.containingFile.classesAndInterfacesAndObjects().isEmpty()
                }
            }

            it("Function is top-level") { goDefendFunc.assertTrue { func -> func.isTopLevel } }

            it("Named correctly") { goDefendFunc.assertTrue { func -> func.name == goDefendName } }

            it("Suppresses naming rule") {
                goDefendFunc.assertTrue { func ->
                    func.hasAnnotation { anno -> anno.text == "@Suppress(\"FunctionNaming\")" }
                }
            }

            it("Function is extension") { goDefendFunc.assertTrue { func -> func.isExtension } }

            it("OtherMessage.Companion receiver") {
                goDefendFunc.assertTrue { func -> func.hasReceiverTypeOf<OtherMessage.Companion>() }
            }

            it("Returns OtherMessage.GoDefend") {
                goDefendFunc.assertTrue { func -> func.hasReturnTypeOf<OtherMessage.GoDefend>() }
            }
        }
    })
