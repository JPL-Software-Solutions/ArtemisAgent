package artemis.agent.cpu

import artemis.agent.game.ObjectEntry
import artemis.agent.game.allies.AllyStatus
import com.walkertribe.ian.vesseldata.VesselData
import com.walkertribe.ian.world.ArtemisNpc
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.clearMocks
import io.mockk.mockk
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@OptIn(ExperimentalSerializationApi::class)
class HailResponseEffectTest :
    DescribeSpec(
        {
            val (testData, ignored) =
                HailResponseEffect::class.java.getResourceAsStream("hail-responses.json")!!.use {
                    Json.decodeFromStream<HailResponseEffectTestData>(it)
                }

            val testDataMap = testData.groupBy { it.response } - HailResponseEffect.OTHER

            val allMessages = testData.flatMap { it.messages }

            val mockVesselData = mockk<VesselData>()

            afterSpec { clearMocks(mockVesselData) }

            describe("HailResponseEffect") {
                describe("Applies to message") {
                    testData.forEach { (response) ->
                        val data = testDataMap[response] ?: return@forEach
                        describe(response.name) {
                            allMessages.forEach { message ->
                                val expected = data.any { it.messages.contains(message) }
                                withData(
                                    nameFn = { "${it.short}: $expected" },
                                    message,
                                    message.hasEnergy(),
                                ) { (long) ->
                                    response.appliesTo(long) shouldBeEqual expected
                                    response(long, null) shouldBeEqual expected
                                }
                            }
                        }
                    }
                }

                describe("Invoke on ally") {
                    val ally = ObjectEntry.Ally(ArtemisNpc(0, 0L), mockVesselData, false)

                    val flyingBlindPartition = testData.partition {
                        it.status == AllyStatus.FLYING_BLIND
                    }
                    val invokeTestData = flyingBlindPartition.run { second + first + second }

                    invokeTestData.forEachIndexed { index, (response, status, messages) ->
                        val expectedStatus =
                            if (index >= testData.size) AllyStatus.FLYING_BLIND else status

                        describe("$response / $status") {
                            withData(nameFn = { it.short }, messages) { (long) ->
                                response(long, ally)
                                ally.status shouldBeEqual expectedStatus
                                ally.hasEnergy.shouldBeEqual(index > 0)
                            }

                            withData(nameFn = { it.short }, messages.map { it.hasEnergy() }) {
                                (long) ->
                                ally.hasEnergy = false
                                response(long, ally)
                                ally.status shouldBeEqual expectedStatus
                                ally.hasEnergy.shouldBeTrue()
                            }
                        }
                    }
                }

                describe("Ignored messages") {
                    withData(
                        nameFn = { it.short },
                        ignored.flatMap { listOf(it, it.hasEnergy()) },
                    ) { (long) ->
                        withData(HailResponseEffect.entries) { response ->
                            response.appliesTo(long).shouldBeFalse()
                        }
                    }
                }

                describe("Ally status") {
                    withData(nameFn = { it.status.name }, testData) { (response, status, messages)
                        ->
                        withData(nameFn = { it.short }, messages) { (long) ->
                            response.getAllyStatus(long) shouldBeEqual status
                        }
                    }
                }
            }
        },
    )
