package pwr.evolutionaryAlgorithm;

import configs.ConfigAnchor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ArrayList;

import pwr.evolutionaryAlgorithm.data.DataLoader;
import java.io.Serializable;

/**
 *
 * Singleton class
 */
public class Configuration implements Serializable {

    static private Configuration Config = null;
    static private int classes = 0;
    static private String PROMPT = "\n [CAREX2.4] ";
    private Report ReportIT;
    private String Comment;                       // comment on research -> see on reports
    private String FileComment;                   // comment on research -> see on reports
    private boolean Fsc = true;
    private CrossoverType CROSSOVER;
    private int CrossValidation = 10;
    private float E_WEIGHT = 0.0f;
    private String TestDataFileName = null;
    private String TrainDataFileName = null;
    private String Image_Blob_Counts_Filename = null;
    private String Image_Blobs_Filename = null;
    private String Image_Doc_Words_Filename = null;
    private String Image_Words_Filename = null;
    private String Image_TEST_Doc_Words = null;
    private String Image_TEST_Blob_Counts = null;
    private String Image_TEST_Blobs = null;
    private boolean ECHO = false;
    private int Selection;                        //0-roullete wheel, 1-random, 2+tournament
    private float Pcrossover;
    private long StopGeneration;
    private float StopEval;
    private float Pmutation;
    private int PopSize;
    private MutationType MUTATION;
    /**/////////////// RuleSet -> Rule -> RuleGene ///////////////////*/
    private int DATASET_RULES = 10;              //number of rules in individual
    private int ACTIVE_CLASS = -1;               //if there is only one active class (>=0) gives its ID, if all classes are active -1
    /**////////// RULE //////////////////////////////////////*/
    private int RULE_ATTRIBUTES = 4;             //number of attributes
    private int RULE_CLASS_BITS = 2;             //number of bits containing class id
    /*//////////////// RuleGene //////////////////////////////
     *   0       1         <- 2-9 ->   <- 10-18 ->  <19-20>
     *  on/off  operator    value1      value2      class_id
     */
    private int RULE_GENE_NO_BITS = 21;
    private int RULE_GENE_OPERATOR_BITS = 1;     // operator bits in/not in
    private int RULE_GENE_VALUE_BITS = 8;        // 8 bits for value
    private int RULE_GENE_VALUES = 2;            // number of values
    private int RULE_GENE_MAX_VALUE = -1;        //max value of value (8 bits -> 255)
    private int TestNumber;
    private String TestFileReport;
    private String TestExFileReport;
    private boolean MUTATION_OF_CLASS = true;
    private float FITNESS_DEFAULT = -99f;
    private boolean coevSubEcho;
    private boolean coevClsEcho;
    private float coevSelCrossoverProb;
    private float coevSelMutationProb;
    private boolean coevEnabled;

    public boolean isCoevSubEcho() {
        return coevSubEcho;
    }

    public void setCoevSubEcho(boolean coevSubEcho) {
        this.coevSubEcho = coevSubEcho;
    }

    public boolean isCoevClsEcho() {
        return coevClsEcho;
    }

    public void setCoevClsEcho(boolean coevClsEcho) {
        this.coevClsEcho = coevClsEcho;
    }

    public float getCoevSelMutationProb() {
        return coevSelMutationProb;
    }

    public void setCoevSelMutationProb(float coevSelMutationProb) {
        this.coevSelMutationProb = coevSelMutationProb;
    }

    public float getCoevSelCrossoverProb() {
        return coevSelCrossoverProb;
    }

    public void setCoevSelCrossoverProb(float coevCrossoverProb) {
        this.coevSelCrossoverProb = coevCrossoverProb;
    }

    private Configuration(String FileName) {
        StringBuilder S = new StringBuilder();
        S.append("configs/");
        S.append(FileName);
        S.append(".properties");
        System.setProperty("file.encoding", "UTF-8");
        Properties properties = new Properties();

        ClassLoader cl = ConfigAnchor.class.getClassLoader();
        InputStream is = cl.getResourceAsStream(S.toString());
        try {
            properties.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ///////////////////////////////////////////////////////

        // Strings
        TestDataFileName = properties.getProperty("TestDataFileName");
        TrainDataFileName = properties.getProperty("TrainDataFileName");
        TestFileReport = properties.getProperty("TEST_REPORT_FILE");
        TestExFileReport = properties.getProperty("TEST_FULL_REPORT_FILE");
        String Mut = properties.getProperty("MUTATION_TYPE");
        String Cross = properties.getProperty("CROSSOVER_TYPE");
        Image_Blob_Counts_Filename = properties.getProperty("IMAGE_Blob_Counts");
        Image_Blobs_Filename = properties.getProperty("IMAGE_Blobs");
        Image_Doc_Words_Filename = properties.getProperty("IMAGE_Doc_Words");
        Image_Words_Filename = properties.getProperty("IMAGE_Words");
        Image_TEST_Doc_Words = properties.getProperty("IMAGE_TEST_Doc_Words");
        Image_TEST_Blob_Counts = properties.getProperty("IMAGE_TEST_Blob_Counts");
        Image_TEST_Blobs = properties.getProperty("IMAGE_TEST_Blobs");
        FileComment = properties.getProperty("FILE_COMMENT");
        // Booleans
        ECHO = Boolean.parseBoolean(properties.getProperty("ECHO"));
        Fsc = Boolean.parseBoolean(properties.getProperty("FSC"));
        coevEnabled = Boolean.parseBoolean(properties.getProperty("coev.enabled"));
        // Floats
        Pcrossover = Float.parseFloat(properties.getProperty("Pcrossover"));
        Pmutation = Float.parseFloat(properties.getProperty("Pmutation"));
        StopEval = Float.parseFloat(properties.getProperty("StopEval"));
        // Integers
        RULE_ATTRIBUTES = Integer.parseInt(properties.getProperty("RULE_ATTRIBUTES"));                  //number of attributes
        RULE_CLASS_BITS = Integer.parseInt(properties.getProperty("RULE_CLASS_BITS"));                  //number of bits containing class id
        RULE_GENE_NO_BITS = Integer.parseInt(properties.getProperty("RULE_GENE_NO_BITS"));
        RULE_GENE_OPERATOR_BITS = Integer.parseInt(properties.getProperty("RULE_GENE_OPERATOR_BITS"));  // operator bits in/not in
        RULE_GENE_VALUE_BITS = Integer.parseInt(properties.getProperty("RULE_GENE_VALUE_BITS"));        // 8 bits for value
        RULE_GENE_VALUES = Integer.parseInt(properties.getProperty("RULE_GENE_VALUES"));                // number of values
        RULE_GENE_MAX_VALUE = Integer.parseInt(properties.getProperty("RULE_GENE_MAX_VALUE"));          // max value of value (8 bits -> 255)
        DATASET_RULES = Integer.parseInt(properties.getProperty("DATASET_RULES"));                      // number of rules in individual
        ACTIVE_CLASS = Integer.parseInt(properties.getProperty("ACTIVE_CLASS"));                        // if there is only one active class (>=0) gives its ID, if all classes are active -1
        Selection = Integer.parseInt(properties.getProperty("Selection"));                              //0-roullete wheel, 1-random, 2+tournament
        PopSize = Integer.parseInt(properties.getProperty("PopSize"));
        CrossValidation = Integer.parseInt(properties.getProperty("CROSSVALIDATION"));
        // XXX modyfikacje potrzebne do API, patrz metoda setCv
        TestNumber = Integer.parseInt(properties.getProperty("TEST_NUMBER"));
        // Long
        StopGeneration = Long.parseLong(properties.getProperty("StopGeneration"));
        ////////// CROSSOVER & MUTATION TYPE //////////////////////////////////////////////////////////
        // COEVOLUTION SPECIFIC
        // Try... catch block maintains backward compability
        try {
            coevSelCrossoverProb = Float.parseFloat(properties.getProperty("coev.sel.crossoverProb"));
            coevSelMutationProb = Float.parseFloat(properties.getProperty("coev.sel.mutationProb"));
            coevClsEcho = Boolean.parseBoolean(properties.getProperty("coev.cls.echo"));
            coevSubEcho = Boolean.parseBoolean(properties.getProperty("coev.sub.echo"));
        } catch (NullPointerException npe) {
            System.out.println("WARN: Coevolution parameters undefined");
        }

        if (Cross.equals("BCX")) {
            CROSSOVER = CrossoverType.BCX;
        } else {
            CROSSOVER = CrossoverType.SCX; //standard crossover
        }
        if (Mut.equals("FAM")) {
            MUTATION = MutationType.FAM;
        } else {
            MUTATION = MutationType.SM; //standard random mutation -> simple mutation
        }
        ReportIT = new Report(TestNumber, CrossValidation, TestFileReport, TestExFileReport);
    }

    public boolean isCoevEnabled() {
        return coevEnabled;
    }

    public void setCoevEnabled(boolean coevEnabled) {
        this.coevEnabled = coevEnabled;
    }

    public enum MutationType {

        SM, FAM;
    };

    public enum CrossoverType {

        SCX, BCX;
    };

    static public void newConfiguration(String S, String ResearchComment) {
        Config = new Configuration(S);
        Config.setResearchComment(ResearchComment);
    }

    static public Configuration getConfiguration() {
        return Config;

    }

    public Report getReport() {
        return ReportIT;
    }

    public void setFsc(boolean F) {
        Config.Fsc = F;
    }

    public boolean isFsc() {
        return Config.Fsc;
    }

    public boolean isEcho() {
        return ECHO;
    }

    public String getPrompt() {
        return PROMPT;
    }

    public int getPopSize() {
        return PopSize;
    }

    public int getSelection() {
        return Selection;
    }

    public float getCrossoverValue() {
        return Pcrossover;
    }

    public float getMutationValue() {
        return Pmutation;
    }

    public CrossoverType getCrossoverType() {
        return CROSSOVER;
    }

    public MutationType getMutationType() {
        return MUTATION;
    }

    public int getCrossvalidationValue() {
        return CrossValidation;
    }

    public float getEWeight() {
        return E_WEIGHT;
    }

    public boolean isImageDataConfiguration() {
        if (Image_Blob_Counts_Filename != null
                && Image_Blobs_Filename != null
                && Image_Doc_Words_Filename != null
                && Image_Words_Filename != null) {
            return true;
        } else {
            return false;
        }

    }

    public String getImageBlobCountsFilename() {
        return Image_Blob_Counts_Filename;
    }

    public String getImageBlobsFilename() {
        return Image_Blobs_Filename;
    }

    public String getImageDocWordFilename() {
        return Image_Blob_Counts_Filename;
    }

    public String getImageWordsFilename() {
        return Image_Words_Filename;
    }

    public String getImageTESTDocWordsFilename() {
        return Image_TEST_Doc_Words;
    }

    public String getImageTESTBlobCountsFilename() {
        return Image_TEST_Blob_Counts;
    }

    public String getImageTESTBlobsFilename() {
        return Image_TEST_Blobs;
    }

    /**
     * number of rules in individual RuleSet
     * @return number of rules in individual
     */
    public int getNumberOfRules() {
        return DATASET_RULES;
    }

    public boolean isOneClassActive() {
        if (ACTIVE_CLASS == -1) {
            return false;
        } else {
            return true;
        }
    }

    public int getActiveClass() {
        return ACTIVE_CLASS;
    }

    public int getNumberOfAttributes() {
        return RULE_ATTRIBUTES;
    }

    public int getMaxValue() {
        if (RULE_GENE_MAX_VALUE == -1) {
            RULE_GENE_MAX_VALUE = (int) Math.pow(2.0f, RULE_GENE_VALUE_BITS) - 1;
        }
        return RULE_GENE_MAX_VALUE;
    }

    public int getNumberOfValues() {
        return RULE_GENE_VALUES;
    }

    public int getRuleGeneNoBits() {
        return RULE_GENE_NO_BITS;
    }

    public int getBitsForOperator() {
        return RULE_GENE_OPERATOR_BITS;
    }

    public int getRuleGeneValueBits() {
        return RULE_GENE_VALUE_BITS;
    }

    public boolean isMutationOfClass() {
        return MUTATION_OF_CLASS;
    }

    public int getClassBits() {
        return RULE_CLASS_BITS;
    }

    public float getFITNESS_DEFAULT() {
        return FITNESS_DEFAULT;
    }

    public String getTestFileName() {
        return this.TestDataFileName;
    }

    public String getTrainFileName() {
        return this.TrainDataFileName;
    }

    public void setTrainDataFileName(String TrainDataFileName) {
        this.TrainDataFileName = TrainDataFileName;
    }

    public void setReportFileName(String filename) {
        TestFileReport = filename;
        ReportIT = new Report(TestNumber, CrossValidation, TestFileReport, TestExFileReport);
    }

    public void setCv(int testsNo, int crossvalidaionNo) {
        this.CrossValidation = crossvalidaionNo;
        this.TestNumber = testsNo;
        ReportIT = new Report(TestNumber, CrossValidation, TestFileReport, TestExFileReport);
    }

    public long getStopGeneration() {
        return this.StopGeneration;
    }

    public float getStopEval() {
        return StopEval;
    }

    public ArrayList<String> getFileNames() {
        /*
         * TODO insert code here
         */
        return null;
    }

    public void setResearchComment(String s) {
        Comment = s.length() > 0 ? s : "";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Configuration config = Configuration.getConfiguration();
        if (this.Comment.length() > 0 || this.FileComment.length() > 0) {
            sb.append("\n----------------------------------------------------------------------");
            if (this.Comment.length() > 0) {
                sb.append("\n  # ").append(this.Comment);
            }
            if (this.FileComment.length() > 0) {
                sb.append("\n  # ").append(this.FileComment);
            }
        }
        sb.append("\n----------------------------------------------------------------------\n");
        sb.append(Configuration.getConfiguration().getPrompt()).append(DataLoader.FileSummary()).append("  ");
        sb.append("\n C(pop=").append(config.getPopSize()).append(", gen=").append(config.getStopGeneration());
        sb.append(", rules=").append(config.getNumberOfRules());
        sb.append(", crossvalidation=").append(config.getCrossvalidationValue());
        sb.append(isFsc() ? " Fit FSC " : " Fit ACC ");
        sb.append(" Pm=").append(Configuration.getConfiguration().getMutationValue());
        sb.append(" ").append(Configuration.getConfiguration().getMutationType());
        sb.append(" Px=").append(Configuration.getConfiguration().getCrossoverValue());
        sb.append(" ").append(Configuration.getConfiguration().getCrossoverType());
        sb.append(" sel=").append(Configuration.getConfiguration().getSelection()).append(" )");
        return sb.toString();
    }

    public String toCSVString() {
        StringBuilder sb = new StringBuilder();
        Configuration cfg = Configuration.getConfiguration();
        // SB.append("file_comment;system;pop_size;stop_gen;rule_no;cross_value
        // ;measure;mutation_val;mutation_type;crossover_value;crossover_type;
        // selection\n");
        sb.append(Comment);
        sb.append(";");
        sb.append(FileComment);
        sb.append(";");
        sb.append("[CAREX2.4]");
        sb.append(";");
        sb.append(cfg.getPopSize());
        sb.append(";");
        sb.append(cfg.getStopGeneration());
        sb.append(";");
        sb.append(cfg.getNumberOfRules());
        sb.append(";");
        sb.append(cfg.getCrossvalidationValue());
        sb.append(";");
        sb.append(isFsc() ? "Fit FSC" : "Fit ACC");
        sb.append(";");
        sb.append(cfg.getMutationValue());
        sb.append(";");
        sb.append(cfg.getMutationType());
        sb.append(";");
        sb.append(cfg.getCrossoverValue());
        sb.append(";");
        sb.append(cfg.getCrossoverType());
        sb.append(";");
        sb.append(cfg.getSelection());
        sb.append(";");
        return sb.toString();
    }

    public void setACTIVE_CLASS(int ACTIVE_CLASS) {
        this.ACTIVE_CLASS = ACTIVE_CLASS;
    }

    public void setCROSSOVER(CrossoverType CROSSOVER) {
        this.CROSSOVER = CROSSOVER;
    }

    public void setComment(String Comment) {
        this.Comment = Comment;
    }

    public void setTestNumber(int TestNumber) {
        this.TestNumber = TestNumber;
    }

    public void setCrossValidation(int CrossValidation) {
        this.CrossValidation = CrossValidation;
    }

    public void setDATASET_RULES(int DATASET_RULES) {
        this.DATASET_RULES = DATASET_RULES;
    }

    public void setECHO(boolean ECHO) {
        this.ECHO = ECHO;
    }

    public void setE_WEIGHT(float E_WEIGHT) {
        this.E_WEIGHT = E_WEIGHT;
    }

    public void setFITNESS_DEFAULT(float FITNESS_DEFAULT) {
        this.FITNESS_DEFAULT = FITNESS_DEFAULT;
    }

    public void setFileComment(String FileComment) {
        this.FileComment = FileComment;
    }

    public void setImage_Blobs_Filename(String Image_Blobs_Filename) {
        this.Image_Blobs_Filename = Image_Blobs_Filename;
    }

    public void setMUTATION(MutationType MUTATION) {
        this.MUTATION = MUTATION;
    }

    public void setMUTATION_OF_CLASS(boolean MUTATION_OF_CLASS) {
        this.MUTATION_OF_CLASS = MUTATION_OF_CLASS;
    }

    public void setPcrossover(float Pcrossover) {
        this.Pcrossover = Pcrossover;
    }

    public void setPmutation(float Pmutation) {
        this.Pmutation = Pmutation;
    }

    public void setPopSize(int PopSize) {
        this.PopSize = PopSize;
    }

    public void setRULE_ATTRIBUTES(int RULE_ATTRIBUTES) {
        this.RULE_ATTRIBUTES = RULE_ATTRIBUTES;
    }

    public void setRULE_CLASS_BITS(int RULE_CLASS_BITS) {
        this.RULE_CLASS_BITS = RULE_CLASS_BITS;
    }

    public void setRULE_GENE_MAX_VALUE(int RULE_GENE_MAX_VALUE) {
        this.RULE_GENE_MAX_VALUE = RULE_GENE_MAX_VALUE;
    }

    public void setRULE_GENE_NO_BITS(int RULE_GENE_NO_BITS) {
        this.RULE_GENE_NO_BITS = RULE_GENE_NO_BITS;
    }

    public void setRULE_GENE_OPERATOR_BITS(int RULE_GENE_OPERATOR_BITS) {
        this.RULE_GENE_OPERATOR_BITS = RULE_GENE_OPERATOR_BITS;
    }

    public void setRULE_GENE_VALUES(int RULE_GENE_VALUES) {
        this.RULE_GENE_VALUES = RULE_GENE_VALUES;
    }

    public void setRULE_GENE_VALUE_BITS(int RULE_GENE_VALUE_BITS) {
        this.RULE_GENE_VALUE_BITS = RULE_GENE_VALUE_BITS;
    }

    public void setSelection(int Selection) {
        this.Selection = Selection;
    }

    public void setStopEval(float StopEval) {
        this.StopEval = StopEval;
    }

    public void setStopGeneration(long StopGeneration) {
        this.StopGeneration = StopGeneration;
    }

    // setters
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Configuration other = (Configuration) obj;
        if ((this.Comment == null) ? (other.Comment != null) : !this.Comment.equals(other.Comment)) {
            return false;
        }
        if ((this.FileComment == null) ? (other.FileComment != null) : !this.FileComment.equals(other.FileComment)) {
            return false;
        }
        if (this.Fsc != other.Fsc) {
            return false;
        }
        if (this.ECHO != other.ECHO) {
            return false;
        }
        if (this.PopSize != other.PopSize) {
            return false;
        }
        if (this.Selection != other.Selection) {
            return false;
        }
        if (this.Pcrossover != other.Pcrossover) {
            return false;
        }
        if (this.Pmutation != other.Pmutation) {
            return false;
        }
        if (this.StopGeneration != other.StopGeneration) {
            return false;
        }
        if (this.StopEval != other.StopEval) {
            return false;
        }
        if (this.MUTATION != other.MUTATION && (this.MUTATION == null || !this.MUTATION.equals(other.MUTATION))) {
            return false;
        }
        if (this.CROSSOVER != other.CROSSOVER && (this.CROSSOVER == null || !this.CROSSOVER.equals(other.CROSSOVER))) {
            return false;
        }
        if (this.CrossValidation != other.CrossValidation) {
            return false;
        }
        if (this.E_WEIGHT != other.E_WEIGHT) {
            return false;
        }
        if ((this.TestDataFileName == null) ? (other.TestDataFileName != null) : !this.TestDataFileName.equals(other.TestDataFileName)) {
            return false;
        }
        if ((this.TrainDataFileName == null) ? (other.TrainDataFileName != null) : !this.TrainDataFileName.equals(other.TrainDataFileName)) {
            return false;
        }
        if ((this.Image_Blob_Counts_Filename == null) ? (other.Image_Blob_Counts_Filename != null) : !this.Image_Blob_Counts_Filename.equals(other.Image_Blob_Counts_Filename)) {
            return false;
        }
        if ((this.Image_Blobs_Filename == null) ? (other.Image_Blobs_Filename != null) : !this.Image_Blobs_Filename.equals(other.Image_Blobs_Filename)) {
            return false;
        }
        if ((this.Image_Doc_Words_Filename == null) ? (other.Image_Doc_Words_Filename != null) : !this.Image_Doc_Words_Filename.equals(other.Image_Doc_Words_Filename)) {
            return false;
        }
        if ((this.Image_Words_Filename == null) ? (other.Image_Words_Filename != null) : !this.Image_Words_Filename.equals(other.Image_Words_Filename)) {
            return false;
        }
        if ((this.Image_TEST_Doc_Words == null) ? (other.Image_TEST_Doc_Words != null) : !this.Image_TEST_Doc_Words.equals(other.Image_TEST_Doc_Words)) {
            return false;
        }
        if ((this.Image_TEST_Blob_Counts == null) ? (other.Image_TEST_Blob_Counts != null) : !this.Image_TEST_Blob_Counts.equals(other.Image_TEST_Blob_Counts)) {
            return false;
        }
        if ((this.Image_TEST_Blobs == null) ? (other.Image_TEST_Blobs != null) : !this.Image_TEST_Blobs.equals(other.Image_TEST_Blobs)) {
            return false;
        }
        if (this.DATASET_RULES != other.DATASET_RULES) {
            return false;
        }
        if (this.ACTIVE_CLASS != other.ACTIVE_CLASS) {
            return false;
        }
        if (this.RULE_ATTRIBUTES != other.RULE_ATTRIBUTES) {
            return false;
        }
        if (this.RULE_CLASS_BITS != other.RULE_CLASS_BITS) {
            return false;
        }
        if (this.RULE_GENE_NO_BITS != other.RULE_GENE_NO_BITS) {
            return false;
        }
        if (this.RULE_GENE_OPERATOR_BITS != other.RULE_GENE_OPERATOR_BITS) {
            return false;
        }
        if (this.RULE_GENE_VALUE_BITS != other.RULE_GENE_VALUE_BITS) {
            return false;
        }
        if (this.RULE_GENE_VALUES != other.RULE_GENE_VALUES) {
            return false;
        }
        if (this.RULE_GENE_MAX_VALUE != other.RULE_GENE_MAX_VALUE) {
            return false;
        }
        if (this.MUTATION_OF_CLASS != other.MUTATION_OF_CLASS) {
            return false;
        }
        if (this.FITNESS_DEFAULT != other.FITNESS_DEFAULT) {
            return false;
        }
        if (this.TestNumber != other.TestNumber) {
            return false;
        }
        if ((this.TestFileReport == null) ? (other.TestFileReport != null) : !this.TestFileReport.equals(other.TestFileReport)) {
            return false;
        }
        if ((this.TestExFileReport == null) ? (other.TestExFileReport != null) : !this.TestExFileReport.equals(other.TestExFileReport)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.ReportIT != null ? this.ReportIT.hashCode() : 0);
        hash = 61 * hash + (this.Comment != null ? this.Comment.hashCode() : 0);
        hash = 61 * hash + (this.FileComment != null ? this.FileComment.hashCode() : 0);
        hash = 61 * hash + (this.Fsc ? 1 : 0);
        hash = 61 * hash + (this.ECHO ? 1 : 0);
        hash = 61 * hash + this.PopSize;
        hash = 61 * hash + this.Selection;
        hash = 61 * hash + Float.floatToIntBits(this.Pcrossover);
        hash = 61 * hash + Float.floatToIntBits(this.Pmutation);
        hash = 61 * hash + (int) (this.StopGeneration ^ (this.StopGeneration >>> 32));
        hash = 61 * hash + Float.floatToIntBits(this.StopEval);
        hash = 61 * hash + (this.MUTATION != null ? this.MUTATION.hashCode() : 0);
        hash = 61 * hash + (this.CROSSOVER != null ? this.CROSSOVER.hashCode() : 0);
        hash = 61 * hash + this.CrossValidation;
        hash = 61 * hash + Float.floatToIntBits(this.E_WEIGHT);
        hash = 61 * hash + (this.TestDataFileName != null ? this.TestDataFileName.hashCode() : 0);
        hash = 61 * hash + (this.TrainDataFileName != null ? this.TrainDataFileName.hashCode() : 0);
        hash = 61 * hash + (this.Image_Blob_Counts_Filename != null ? this.Image_Blob_Counts_Filename.hashCode() : 0);
        hash = 61 * hash + (this.Image_Blobs_Filename != null ? this.Image_Blobs_Filename.hashCode() : 0);
        hash = 61 * hash + (this.Image_Doc_Words_Filename != null ? this.Image_Doc_Words_Filename.hashCode() : 0);
        hash = 61 * hash + (this.Image_Words_Filename != null ? this.Image_Words_Filename.hashCode() : 0);
        hash = 61 * hash + (this.Image_TEST_Doc_Words != null ? this.Image_TEST_Doc_Words.hashCode() : 0);
        hash = 61 * hash + (this.Image_TEST_Blob_Counts != null ? this.Image_TEST_Blob_Counts.hashCode() : 0);
        hash = 61 * hash + (this.Image_TEST_Blobs != null ? this.Image_TEST_Blobs.hashCode() : 0);
        hash = 61 * hash + this.DATASET_RULES;
        hash = 61 * hash + this.ACTIVE_CLASS;
        hash = 61 * hash + this.RULE_ATTRIBUTES;
        hash = 61 * hash + this.RULE_CLASS_BITS;
        hash = 61 * hash + this.RULE_GENE_NO_BITS;
        hash = 61 * hash + this.RULE_GENE_OPERATOR_BITS;
        hash = 61 * hash + this.RULE_GENE_VALUE_BITS;
        hash = 61 * hash + this.RULE_GENE_VALUES;
        hash = 61 * hash + this.RULE_GENE_MAX_VALUE;
        hash = 61 * hash + (this.MUTATION_OF_CLASS ? 1 : 0);
        hash = 61 * hash + Float.floatToIntBits(this.FITNESS_DEFAULT);
        hash = 61 * hash + this.TestNumber;
        hash = 61 * hash + (this.TestFileReport != null ? this.TestFileReport.hashCode() : 0);
        hash = 61 * hash + (this.TestExFileReport != null ? this.TestExFileReport.hashCode() : 0);
        return hash;
    }

    public static void setClasses(int classes) {
        Configuration.classes = classes;
    }

    static public int getClassesNo() {
        return classes;
    }

    static public void setClassesNo(int i) {
        classes = i;
    }

    public static void setConfiguration(Configuration configuration) {
        Config = configuration;
    }

    public static void setPROMPT(String PROMPT) {
        Configuration.PROMPT = PROMPT;
    }
}
