package pwr.evolutionaryAlgorithm.data;

import pwr.evolutionaryAlgorithm.Configuration;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author pawel
 */
public class DataLoader {

    protected static String TrainDataFileName;
    protected static String TestDataFileName;
    protected static DataSource TrainData;
    protected static DataSource TestData;
    static private int Data_arguments;
    static protected ArrayList<String> Data_ClassesNames;
    static private float Data_arg_min[];
    static private float Data_arg_max[];
    static private int crossValidationUpdate = 0;
    static private ArrayList<ArrayList<Record>> CVstructure;

    /*
     * spit randomly data into N parts
     * before that all data is in TrainData
     */
    public static void doCrossvalidation() {

        //init
        crossValidationUpdate = 0;
        int parts = Configuration.getConfiguration().getCrossvalidationValue();
        CVstructure = new ArrayList<ArrayList<Record>>(parts);
        for (int i = 0; i < parts; i++) {
            CVstructure.add(i, new ArrayList<Record>());
        }

        //spit data
        DataSource AllData = new DataSource(TrainData);
        AllData.OrganizeData();
        int elements = AllData.size() / parts;
        for (int p = 0; p < parts; p++) {
            for (int e = 0; e < elements; e++) {
                Record Rd = AllData.removeRandomRecord();
                CVstructure.get(p).add(Rd);
            }
        }
        if (AllData.size() > 0) {
            int rest = AllData.size();
            for (int r = 0; r < rest; r++) {
                Record Rd = AllData.removefirstRecord();
                CVstructure.get(r).add(Rd);
            }
        }

        for (int i = 0; i < CVstructure.size(); i++) {
            int s = CVstructure.get(i).size();
            System.out.print(" " + i + "[" + s + "] ");
        }
        /// 0 TEST 1...N TRAIN
        // 0 - fill Test
        // p=1..N-1 - fil train
        initCrossvalidationFold(0);
    }

    public static void doCrossvalidationNext() {
        crossValidationUpdate++;
        initCrossvalidationFold(crossValidationUpdate);
    }

    private static void initCrossvalidationFold(int fold) {
        int parts = Configuration.getConfiguration().getCrossvalidationValue();
        TestData.clear();
        TrainData.clear();
        for (int part = 0; part < parts; part++) {
            for (Record r : CVstructure.get(part)) {
                if (part == fold) {
                    TestData.addRecord(r);
                } else {
                    TrainData.addRecord(r);
                }
            }
        }
        TestData.OrganizeData();
        TrainData.OrganizeData();
    }

    public static int getArgumentsNo() {
        return Data_arguments;
    }

    public static int getClassId(String S) {
        return Data_ClassesNames.indexOf(S);
    }

    public static DataSource getTrainData() {
        return TrainData;
    }

    public static DataSource getTestData() {
        return TestData;
    }

    public static int getClassNumber() {
        return Data_ClassesNames.size();
    }

    public static String getClassName(int i) {
        return Data_ClassesNames.get(i);
    }

    static float getArgMin(int argID) {
        return Data_arg_min[argID];
    }

    static float getArgMax(int argID) {
        return Data_arg_max[argID];
    }

    public static DataLoader getDataLoader(Configuration config) {
        if (config.isImageDataConfiguration() == true) {
            /**
             * todo: only ECCV_2002 -- insert universal code here
             */
            return new DataLoader(config.getImageWordsFilename(),
                    config.getImageDocWordFilename(),
                    config.getImageBlobCountsFilename(),
                    config.getImageBlobsFilename(),
                    config.getImageTESTDocWordsFilename(),
                    config.getImageTESTBlobCountsFilename(),
                    config.getImageTESTBlobsFilename());
        } else {
            return new DataLoader(
                    config.getTrainFileName(),
                    config.getTestFileName());
        }
    }

    /**
     * main constructor of DataSource class
     * @param TrainDataFileName_ name of file with data for training
     * @param TestDataFileName_ name of file with data for tests
     */
    public DataLoader(String TrainDataFileName_, String TestDataFileName_) {
        TestDataFileName = TestDataFileName_;
        TrainDataFileName = TrainDataFileName_;

        TestData = new DataSource();
        TrainData = new DataSource();

        Data_ClassesNames = new ArrayList<String>();

        Data_arg_max = null;
        Data_arg_min = null;

        TrainData = this.LoadCSVFile(TrainDataFileName);
        if (TestDataFileName.length() != 0) {
            TestData = this.LoadCSVFile(TestDataFileName);
        }

        Configuration.setClassesNo(getClassNumber());

        //@todo: CROSSVALIDATION on GRAPHical files 
        //else SplitData( Configuration.getConfiguration().getCrossvalidationValue() );
        //TrainData.OrganizeData();
        //TestData.OrganizeData();
    }

    public DataLoader(String words,
            String document_words,
            String blob_counts,
            String blobs,
            String test_document_words,
            String test_blob_counts,
            String test_blobs) {

        TestDataFileName = "";
        TrainDataFileName = "";

        TestData = new DataSource();
        //TrainData = new DataSource();

        Data_ClassesNames = new ArrayList<String>();

        Data_arg_max = null;
        Data_arg_min = null;

        TrainData = LoadImages(words, document_words, blob_counts, blobs, false);

        if (test_document_words.length() != 0) {
            TestData = LoadImages(words, test_document_words, test_blob_counts, test_blobs, true);
        }
        //@todo: CROSSVALIDATION on GRAPHical files 
        //else SplitData( Configuration.getConfiguration().getCrossvalidationValue() );
        //TrainData.OrganizeData();
        //TestData.OrganizeData();

        Configuration.setClassesNo(getClassNumber());
    }

    /// separator <tab> -> each value in ", eg. "1.0" end line -> new record
    private DataSource LoadCSVFile(String FileName) {
        DataSource DS = new DataSource();
        try {
            BufferedReader br = new BufferedReader(new FileReader(FileName));
            String strLine = br.readLine();
            int from, to;
            ///////////////////// number of fields? ///////////////////////
            Data_arguments = strLine.split("\t").length - 1;
            //////////////////////////////////////////////////////////////////////////

            boolean MinMaxWasNull = false;
            if (Data_arg_max == null) {
                Data_arg_max = new float[Data_arguments];
                MinMaxWasNull = true;
            }
            if (Data_arg_min == null) {
                Data_arg_min = new float[Data_arguments];
                MinMaxWasNull = true;
            }


            ///// READ FIELDS //////////////////////////////////////////
            int id = 0; // Coevolution
            while (strLine != null && strLine.length() != 0) {
                from = 0;
                to = 0;
                float[] tab = new float[Data_arguments];
                for (int i = 0; i < Data_arguments; i++) {
                    to = strLine.indexOf("\t", from);
                    tab[i] = Float.valueOf(strLine.substring(from + 1, to - 1)).floatValue();
                    from = to + 1;

                    ///////////////////// MIN & MAX values /////////////////////

                    float tym_value = tab[i];

                    if (i == 0 && MinMaxWasNull == true) {
                        Data_arg_max[i] = Data_arg_min[i] = tym_value;  //initalisation
                    }
                    if (Data_arg_max[i] < tym_value) {
                        Data_arg_max[i] = tym_value;
                    }
                    if (Data_arg_min[i] > tym_value) {
                        Data_arg_min[i] = tym_value;
                    }

                    ////////////////////////////////////////////////////////////
                }
                //// READ class ID //////////////////////////////////////
                String StringClassID = strLine.substring(from + 1, strLine.length() - 1);
                if (!Data_ClassesNames.contains(StringClassID)) {
                    Data_ClassesNames.add(StringClassID);
                }
                int class_id_ = DataLoader.getClassId(StringClassID);

                DS.addRecord(new Record(id++, tab, class_id_));
                strLine = br.readLine();
            }
            //Close the input stream
            br.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return DS;
    }

    /**
     * makes a DataSource from gieven set of 4 files (only for images!)
     * @param words filename
     * @param document_words name of file that connects words and images
     * @param blob_counts name of file that contains number of segments in each image
     * @param blobs name of file that contain segments
     * @return DataSource with all data
     */
    public DataSource LoadImages(String words, String document_words, String blob_counts, String blobs, boolean test) {
        DataSource DS = new DataSource();

        //////////////READING & REGISTERING WORDS.... //////////////////////////
        try {
            Data_ClassesNames.add(null); //there is no 0 class
            FileInputStream fstream = new FileInputStream(words);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine = br.readLine();
            while (strLine != null) {
                if (!Data_ClassesNames.contains(strLine)) {
                    Data_ClassesNames.add(strLine);
                }
                strLine = br.readLine();
            }
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

///////////// READING IMAGES & SEGMENTS ////////////////////////////////////////
        try {
            FileInputStream fstreamDocWords = new FileInputStream(document_words);
            DataInputStream in = new DataInputStream(fstreamDocWords);
            BufferedReader brWords = new BufferedReader(new InputStreamReader(in));
            String strLineWords = brWords.readLine();

            ///////////////////// number of words? ///////////////////////
            int WordsInImage = 0;
            int from = 0, to = 0;
            do {
                to = strLineWords.indexOf(" ", from);
                WordsInImage++;
                from = to + 1;
            } while (to < strLineWords.lastIndexOf(" "));
            ////////////////////////////////////////////////////////////////////
            if (test == false) {
                Data_arguments = Configuration.getConfiguration().getNumberOfAttributes();
                if (Data_arg_max == null) {
                    Data_arg_max = new float[Data_arguments];
                }
                if (Data_arg_min == null) {
                    Data_arg_min = new float[Data_arguments];
                }
            }

            /**
             * todo:     data
             *
             * boolean MinMaxWasNull = false;
            if (Data_arg_max == null)  {Data_arg_max = new float[Data_arguments];MinMaxWasNull=true;}
            if (Data_arg_min == null)  {Data_arg_min = new float[Data_arguments];MinMaxWasNull=true;}

             *
             */
            /////////////// Open file with number of semegents
            FileInputStream fstreamDocSementsInfo = new FileInputStream(blob_counts);
            DataInputStream in2 = new DataInputStream(fstreamDocSementsInfo);
            BufferedReader brSegementsInfo = new BufferedReader(new InputStreamReader(in2));

            ///////////// open file with segments.............
            FileInputStream fstreamDocSements = new FileInputStream(blobs);
            DataInputStream in3 = new DataInputStream(fstreamDocSements);
            BufferedReader brSegements = new BufferedReader(new InputStreamReader(in3));

            while (strLineWords != null) {

                /////////// number of segments?
                String strLineSegmentsInfo = brSegementsInfo.readLine();
                int segments = Integer.parseInt(strLineSegmentsInfo);
                String[] segmentsTab = new String[segments];
                for (int i = 0; i < segments; i++) {
                    String strLineSegments = brSegements.readLine();
                    segmentsTab[i] = strLineSegments;
                }
                RecordImage R = new RecordImage(strLineWords, segmentsTab);
                DS.addRecord(R);
                ///////////////////////// for each argument.....
                for (int a = 0; a < Data_arguments; a++) {
                    if (DS.size() == 1) { ///first record
                        Data_arg_max[a] = R.getMaxAttribValue(a);
                        Data_arg_min[a] = R.getMinAttribValue(a);
                    }
                    if (R.getMaxAttribValue(a) > Data_arg_max[a]) {
                        Data_arg_max[a] = R.getMaxAttribValue(a);
                    }
                    if (R.getMinAttribValue(a) < Data_arg_min[a]) {
                        Data_arg_min[a] = R.getMinAttribValue(a);
                    }
                }
                ///////////////////////////////////////////////////////////////
                strLineWords = brWords.readLine();
            }
            //Close the input streams
            in.close();
            in2.close();
            in3.close();

            Data_arguments = Configuration.getConfiguration().getNumberOfAttributes();

        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        ///////////////////////////////////////

        return DS;
    }

    public static String FileSummary() {
        StringBuilder s = new StringBuilder("");
        s.append(" dataFile:");
        s.append(TrainDataFileName);
        s.append(" inst.:");
        s.append(TrainData.size());
        s.append(" atrib:");
        s.append(Data_arguments);
        s.append(" classes:");
        s.append(Data_ClassesNames.size());
        s.append(" [");
        for (int i = 0; i < Data_ClassesNames.size(); i++) {
            s.append(" " + i + "'" + Data_ClassesNames.get(i) + "'");
        }
        s.append(" ]");
        s.append(" Data(train=");
        s.append(TrainData.size());
        s.append(", test=");
        s.append(TestData.size());
        s.append(")");
        return s.toString();
    }
}
