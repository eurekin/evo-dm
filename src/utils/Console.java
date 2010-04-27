package utils;

import EvolutionaryAlgorithm.EvoAlgorithm;



// <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
// #[regen=yes,id=DCE.90A8F900-3180-78C3-E6B4-ABEA113A3C1E]
// </editor-fold> 
public class Console {

    public static enum CommandType { NOTHING, START, PAUSE , CONTINUE, RESTART,  SAVE_STOP, STOP ;}

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.38D19500-B866-4B2A-092B-1BD5486B0EBC]
    // </editor-fold> 
    private EvoAlgorithm mEvoAlgorithm;

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.A456FECF-452C-DAF5-C13F-8687F8B89DD5]
    // </editor-fold> 
    public Console () {
    }

    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,id=DCE.63FA501B-22D4-B27F-176F-3988EC35FDF5]
    // </editor-fold> 
    /**
     *
     * @param Cmd
     */
    public void Command(CommandType Cmd) {
    }

    
    // <editor-fold defaultstate="collapsed" desc=" UML Marker "> 
    // #[regen=yes,regenBody=yes,id=DCE.3F18BCDA-0F8E-0588-80BC-94BFD0F5CB55]
    // </editor-fold> 
    public EvoAlgorithm getEvoAlgorithm () {
        return mEvoAlgorithm;
    }

}

