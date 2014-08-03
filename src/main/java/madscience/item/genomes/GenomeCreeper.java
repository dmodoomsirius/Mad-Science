package madscience.item.genomes;

import madscience.factory.mod.MadMod;

public class GenomeCreeper extends ItemGenomeBase
{

    public GenomeCreeper(int id, int primaryColor, int secondaryColor)
    {
        super(id, primaryColor, secondaryColor);
        this.setCreativeTab(MadMod.getCreativeTab());
    }

}
