package be.tapped.vlaamsetv.browse.vrt

import be.tapped.vlaamsetv.browse.presenter.Item
import be.tapped.vrtnu.content.Program

class ProgramMapper {

    fun toImageCard(index: Int, program: Program): Item.ImageCard =
        Item.ImageCard(
            index = index,
            title = program.title,
            description = program.description,
            imageViewUrl = program.thumbnail
        )
}
