package ru.kicker721.happybirthday

import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit

class KonfettiPresets {
    companion object {

        fun rain(drawable: Shape.DrawableShape? = null): List<Party> {
            return listOf(
                Party(
                    size = listOf(Size(sizeInDp = 15, mass = 8f), Size.MEDIUM, Size.LARGE),
                    shapes = listOf(drawable).filterNotNull(),
                    speed = 0f,
                    maxSpeed = 15f,
                    damping = 0.9f,
                    timeToLive = 3000,
                    angle = Angle.BOTTOM,
                    spread = Spread.ROUND,
                    colors = listOf(0x00ff00, 0xbf00ff),
                    emitter = Emitter(duration = 10, TimeUnit.SECONDS).perSecond(100),
                    position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0))
                )
            )
        }
    }
}