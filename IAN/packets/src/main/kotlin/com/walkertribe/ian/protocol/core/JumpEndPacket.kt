package com.walkertribe.ian.protocol.core

import com.walkertribe.ian.iface.PacketReader
import com.walkertribe.ian.protocol.PacketSubtype

@PacketSubtype(subtype = SimpleEventPacket.Subtype.JUMP_END)
class JumpEndPacket(reader: PacketReader) : SimpleEventPacket(reader) {
    override val details: String = ""
}
