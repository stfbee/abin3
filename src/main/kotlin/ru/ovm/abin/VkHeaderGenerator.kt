package ru.ovm.abin

import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.io.File
import javax.imageio.ImageIO

class VkHeaderGenerator {
    fun drawText(groups_count: String, albums_count: String, items_count: String, result_path: String) {
        val template = File("abin_header_template.png")
        val fontFile = File("RobotoLight.ttf")

        val bufferedImage = ImageIO.read(template)
        val graphics = bufferedImage.createGraphics()
        graphics.color = Color.WHITE
        var font = Font.createFont(Font.TRUETYPE_FONT, fontFile)
        font = font.deriveFont(Font.PLAIN, 60f)
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        graphics.font = font
        val fontMetrics = graphics.fontMetrics
        graphics.drawString(groups_count, 1301 - fontMetrics.stringWidth(groups_count), 133)
        graphics.drawString(albums_count, 1301 - fontMetrics.stringWidth(albums_count), 248)
        graphics.drawString(items_count, 1301 - fontMetrics.stringWidth(items_count), 363)
        ImageIO.write(bufferedImage, "png", File(result_path))
    }
}
