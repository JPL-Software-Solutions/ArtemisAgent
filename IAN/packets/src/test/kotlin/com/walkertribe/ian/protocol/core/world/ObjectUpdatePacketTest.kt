package com.walkertribe.ian.protocol.core.world

import com.walkertribe.ian.enums.ObjectType
import com.walkertribe.ian.protocol.core.PacketTestSpec
import com.walkertribe.ian.protocol.core.TestPacketTypes
import io.kotest.core.spec.style.scopes.DescribeSpecContainerScope
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.Gen
import io.kotest.property.exhaustive.bytes
import io.kotest.property.exhaustive.filterNot
import io.kotest.property.exhaustive.map
import io.ktor.utils.io.core.buildPacket
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.io.Source
import kotlinx.io.writeIntLe

class ObjectUpdatePacketTest :
    PacketTestSpec.Server<ObjectUpdatePacket>(
        specName = "ObjectUpdatePacket",
        fixtures = ObjectUpdatePacketFixture.ALL,
        failures =
            listOf(
                object :
                    Failure(
                        TestPacketTypes.OBJECT_BIT_STREAM,
                        "Fails to parse invalid object type",
                    ) {
                    private val validObjectTypeIDs =
                        ObjectType.entries.map { it.id }.toSet() + setOf(0)

                    override val payloadGen: Gen<Source> =
                        Exhaustive.bytes()
                            .filterNot { validObjectTypeIDs.contains(it) }
                            .map { buildPacket { writeIntLe(it.toInt()) } }
                }
            ),
    ) {
    override fun DescribeSpecContainerScope.describeMore(): Job = launch {
        describe("List of parsers") {
            withData(
                nameFn = { it.first },
                Triple("Player ship", 1, PlayerShipParser),
                Triple("Weapons", 2, WeaponsParser),
                Triple("Engineering", 3, UnobservedObjectParser.Engineering),
                Triple("Upgrades", 4, UpgradesParser),
                Triple("NPC ship", 5, NpcShipParser),
                Triple("Base", 6, BaseParser),
                Triple("Mine", 7, MineParser),
                Triple("Anomaly", 8, UnobservedObjectParser.Anomaly),
                Triple("Nebula", 10, UnobservedObjectParser.Nebula),
                Triple("Torpedo", 11, UnobservedObjectParser.Torpedo),
                Triple("Black hole", 12, BlackHoleParser),
                Triple("Asteroid", 13, UnobservedObjectParser.Asteroid),
                Triple("Generic mesh", 14, UnobservedObjectParser.GenericMesh),
                Triple("Creature", 15, CreatureParser),
                Triple("Drone", 16, UnobservedObjectParser.Drone),
            ) { (_, id, expectedParser) ->
                ObjectUpdatePacket.PARSERS[id.toByte()] shouldBe expectedParser
            }
        }
    }
}
