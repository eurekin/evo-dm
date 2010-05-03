
package pwr.evolutionaryAlgorithm.individual;
import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.data.Condition;
import pwr.evolutionaryAlgorithm.utils.*;
import java.util.*;


/**
 * Class containing Rules <>---- RuleGene
 * @author bred
 */
class RuleGene {
   private BitSet Genes;
   
   static private boolean normalized = true;

   private Condition Cond = null;

    public RuleGene(){
       this.Genes = new BitSet(Configuration.getConfiguration().getRuleGeneNoBits());
        for (int i=0;i<Configuration.getConfiguration().getRuleGeneNoBits();i++){
            boolean v = Rand.GetRandomBoolean();
            this.Genes.set(i,v);
        }
       Cond = null;
    }

    public RuleGene( final RuleGene g){
        this.Genes = new BitSet(Configuration.getConfiguration().getRuleGeneNoBits());
        for (int i=0;i<Configuration.getConfiguration().getRuleGeneNoBits();i++){
            boolean v = g.Genes.get(i);
            this.Genes.set(i,v);
        }
        Cond = null;
    }

    public void Mutation(){
        for (int i=0;i<Configuration.getConfiguration().getRuleGeneNoBits();i++){
            if (Rand.GetRandomBooleanFlip( Configuration.getConfiguration().getMutationValue())){
                this.Genes.flip(i);
            }
        Cond = null;
        }
    }

    /**
     * random initalisation
     */
    public void Initialisation(){
        for (int i=0;i<this.Genes.length();i++){
            this.Genes.set(i, Rand.GetRandomBooleanFlip(0.5f) );
        }
        Cond = null;
    }
    
    public boolean isOff(){
        return (this.Genes.get(0));
    }

    /**
     * (TMP) method in simple form (only in/not in)
     * @return type of relation
     */
    private Condition.RelationType getRelation(){
        if (this.Genes.get(1)==true)  return Condition.RelationType.IN;
        else return Condition.RelationType.NOT_IN;
    }

    /**
     * Interpetation as condition a given Rule
     * @param attribID takes atribute id
     * @return Condition object as interpretation of given atribute
     */
    public Condition getCondition(int attribID){
        //laezy build
        if (Cond != null) return Cond;

        /////// Binary
        int offset = 1+Configuration.getConfiguration().getBitsForOperator();
        //float value1 = BinaryCode.getFloatFromBinary( this.Genes.get( offset, offset+Configuration.getRuleGeneValueBits() ) );
        //float value2 = BinaryCode.getFloatFromBinary( this.Genes.get(offset+Configuration.getRuleGeneValueBits(), offset+Configuration.getRuleGeneValueBits()*Configuration.getNumberOfValues()) );
        
      float value1 = BinaryCode.GrayToFloat(  this.Genes.get( offset, offset+Configuration.getConfiguration().getRuleGeneValueBits() ) );
      float value2 = BinaryCode.GrayToFloat( this.Genes.get(offset+Configuration.getConfiguration().getRuleGeneValueBits(), offset+Configuration.getConfiguration().getRuleGeneValueBits()*Configuration.getConfiguration().getNumberOfValues()) );

         if (normalized ) {
               value1 = value1/Configuration.getConfiguration().getMaxValue();
               value2 = value2/Configuration.getConfiguration().getMaxValue();
         }

        if (value1>value2) {
            float tym = value1;
            value1 = value2;
            value2 = tym;
        }
       Condition.RelationType r = this.getRelation();
       try{
       Cond = new Condition(attribID, r, value1, value2);
       }
       catch (Exception e){
       System.out.println(e.getMessage());
       }
       return Cond;
    }

    public String toString(int atrib){
       StringBuilder s = new StringBuilder("");
        /*for (int i =0; i<size; i++){
            if (this.Genes.get( i )) s.append("1");
            else s.append("0");
        }*/
     if (this.isOff()) s.append(" OFF ");
     else s.append( this.getCondition(atrib).toString() );
      return s.toString();
    }
    
    //------------------------------------------------------------------------------
    
    public int Diverse( final RuleGene g){
    	int diff = 0;
        for (int i=0;i<Configuration.getConfiguration().getRuleGeneNoBits();i++){
              if (this.Genes.get(i)!=g.Genes.get(i)) diff++;
            }	
        return diff;
    }
    
    //------------------------------------------------------------------------------

}
