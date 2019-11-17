package virtualMachine

import kotlin.math.pow

object Opc {
    val BR: Byte = 0b0000
    val ADD: Byte = 0b0001
    val LD: Byte = 0b0010
    val ST: Byte = 0b0011
    val JSR: Byte = 0b0100
    val AND: Byte = 0b0101
    val LDG: Byte = 0b0110
    val STR: Byte = 0b0111
    val RTI: Byte = 0b1000
    val NOT: Byte = 0b1001
    val LDI: Byte = 0b1010
    val STI: Byte = 0b1011
    val JUMP: Byte = 0b1100
    val RES: Byte = 0b1101
    val LEA: Byte = 0b1110
    val TRAP: Byte = 0b1111
}

fun signExtend(x: Int, bitCount: Int): Int {
    if (x.shr(bitCount - 1) and 0b1 == 1) {
        return (0b1111_1111_1111_1111 shl bitCount) or x
    } else {
        return x
    }
}

val R_R0 = 0
val R_R1 = 1
val R_R2 = 2
val R_R3 = 3
val R_R4 = 4
val R_R5 = 5
val R_R6 = 6
val R_R7 = 7
val R_PC = 8
val R_CND = 9
val R_COUNT = 10

val FL_POS = 1 shl 0
val FL_ZRO = 1 shl 1
val FL_NEG = 1 shl 2

val MEM_SIZE = 2.toDouble().pow(16).toInt()

fun main() {
    println("vm")
    val Registers: Array<Int> = Array(R_COUNT) { 0 }
    val Memory: Array<Int> = Array(MEM_SIZE) { 0 }

    fun memRead(address: Int): Int {
        return Memory[address]
    }

    fun updateFlags(registerAddress: Byte): Unit {
        val value = Registers[registerAddress.toInt()]
        if (value == 0) {
            Registers[R_CND] = FL_ZRO
        } else if (value shr 15 == 1) {
            Registers[R_CND] = FL_NEG
        } else {
            Registers[R_CND] = FL_POS
        }
    }

    fun ADD(instruction: Int) {
        val DR: Byte = (instruction.shr(9) and 0b111).toByte()
        val R1: Byte = (instruction.shr(6) and 0b111).toByte()
        val cnd: Byte = (instruction.shr(5) and 0b1).toByte()

        if (cnd == 1.toByte()) {
            val R2: Byte = (instruction and 0b111).toByte()
            Registers[DR.toInt()] = (Registers[R1.toInt()] + Registers[R2.toInt()]) and 0XFFFF
        } else if (cnd == 0.toByte()) {
            val R2: Byte = (instruction and 0b11111).toByte()
            Registers[DR.toInt()] = (Registers[R1.toInt()] + signExtend(R2.toInt(), 5)) and 0XFFFF
        }
    }

    fun LDI(instruction: Int) {
        val DR: Byte = (instruction.shr(9) and 0b111).toByte()
        val PCoffset: Int = signExtend((instruction and 0b1_1111_1111), 9)
        val PC = Registers[R_PC]
        val DM = (PC + PCoffset) and 0xFFFF
        Registers[DR.toInt()] = memRead(memRead(DM))
        updateFlags(DR)
    }

    val instruction: Int = 0b1101_001_001_1_11111
    val opcode: Byte = (instruction.shr(12) and 0b1111).toByte()

    when (opcode) {
        Opc.BR -> {
        }
        Opc.ADD -> {
            ADD(instruction)
        }
        Opc.LD -> {
        }
        Opc.ST -> {
        }
        Opc.JSR -> {
        }
        Opc.AND -> {
        }
        Opc.LDG -> {
        }
        Opc.STR -> {
        }
        Opc.RTI -> {
        }
        Opc.NOT -> {
        }
        Opc.LDI -> {
            LDI(instruction)
        }
        Opc.STI -> {
        }
        Opc.JUMP -> {
        }
        Opc.RES -> {
        }
        Opc.LEA -> {
        }
        Opc.TRAP -> {
        }
    }
}
