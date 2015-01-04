package de.mida.vidorg;

import java.io.*;

/**
 * Created by HP on 03.01.15.
 */
public class FLVMetaDataParser {

    protected String path;

    //// FLV-Header
    /**
     * File version (for example, 0x01 for FLV version 1)
     */
    protected int Version;
    /**
     * Audio tags are present
     */
    protected int TypeFlagsAudio;
    /**
     * Video tags are present
     */
    protected int TypeFlagsVideo;
    /**
     * Offset in bytes from start of file to start of body (that is, size of header)
     */
    protected int[] DataOffset = new int[4];
    protected int DataOffsetInt;

    //// FLV-Body
    /**
     * First tag
     */
    protected FlvTag Tag1;
    /**
     * Size of previous tag, including its header.
     * For FLV version 1, this value is 11 plus the
     * DataSize of the previous tag.
     */
    protected byte[] PreviousTagSize1 = new byte[4];
    /**
     * Second tag
     */
    protected FlvTag Tag2;
    /**
     * Size of second-to-last tag
     */
    protected byte[] PreviousTagSizeN_1 = new byte[4];
    /**
     * Last tag
     */
    protected FlvTag TagN;
    /**
     * Size of last tag
     */
    protected byte[] PreviousTagSizeN = new byte[4];

    //// AUDIODATA
    /**
     * Format of SoundData
     */
    protected byte[] SoundFormat = new byte[4];

    /**
     * Set path to video file and read meta data.
     *
     * @param path Path to video file
     */
    public FLVMetaDataParser(String path) {
        this.path = path;
        this.readMetaData();
    }

    public static void main(String[] args) {
        FLVMetaDataParser mde = new FLVMetaDataParser("p:\\tatort\\ARD Mediathek Tatort - Borowski und der freie Fall (FSK  tgl- ab 20 Uhr) - Sonntag, 14-10-2012  Das Erste.flv");
        mde.printMetaData();
    }

    /**
     * Codesnippet from http://snippets.dzone.com/posts/show/94
     * <p/>
     * Convert the byte array to an int.
     *
     * @param b The byte array
     * @return The integer
     */
    public static int byteArrayToInt(byte[] arr) {
        int number = 0;
        for (int i = 0; i < arr.length; ++i) {
            number |= (arr[arr.length - 1 - i] & 0xff) << (i << arr.length - 1);
        }

        return number;
    }

    public static int byteArrayToInt(int[] arr) {
        int number = 0;
        for (int i = 0; i < arr.length; ++i) {
            number |= (arr[arr.length - 1 - i] & 0xff) << (i << arr.length - 1);
        }

        return number;
    }

    /**
     * Writes data from FLVTAG into stdout.
     *
     * @param ft FlvTag
     */
    public static void printFlvTag(FlvTag ft) {
        System.out.println(
                "FlvTag() {\n" +
                        "\tTagType = " + toHexString(ft.TagType) + "\n" +
                        "\tDataSize = " + toHexString(ft.DataSize) + "\n" +
                        "\tTimestamp = " + toHexString(ft.Timestamp) + "\n" +
                        "\tTimestampExtended = " + toHexString(ft.TimestampExtended) + "\n" +
                        "\tStreamID = " + toHexString(ft.StreamID) + "\n" +
                        "\tData = " + ft.Data + "\n}"
        );
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    /**
     * Write all meta data to stdout.
     */
    public void printMetaData() {
        System.out.println(
                "+++ FLV-HEADER +++\n" +
                        "UI8 Version = " + this.Version + "\n" +
                        "UB[1] TypeFlagsAudio = " + this.TypeFlagsAudio + "\n" +
                        "UB[1] TypeFlagsVideo = " + this.TypeFlagsVideo + "\n" +
                        "UI32 DataOffset = " + Integer.toHexString(byteArrayToInt(DataOffset)) + "\n"
        );


        System.out.print(
                "+++ FLV-BODY +++\n" +
                        "FLVTAG Tag1 = "
        );
        printFlvTag(this.Tag1);
        System.out.print(
                "UI32 PreviousTagSize1 = " + byteArrayToInt(PreviousTagSize1) + " > " + toHexString(PreviousTagSize1) + "\n" +
                        "FLVTAG Tag2 = "
        );
        printFlvTag(this.Tag2);
        System.out.print(
                "UI32 PreviousTagSizeN-1 = " + byteArrayToInt(PreviousTagSizeN_1) + " > " + toHexString(PreviousTagSizeN_1) + "\n" +
                        "FLVTAG TagN = "
        );
        printFlvTag(this.TagN);
        System.out.print(
                "UI32 PreviousTagSizeN = " + byteArrayToInt(PreviousTagSizeN) + " > " + toHexString(PreviousTagSizeN) + "\n");

        System.out.println(
                "\n+++ AUDIODATA +++\n" +
                        "UB[4] SoundFormat = " + Integer.toHexString(byteArrayToInt(SoundFormat)));
    }

    /**
     * Read FLVTAG data block.
     *
     * @param is instance of InputStream for the video file
     * @return Structure of FlvTag
     * @throws IOException
     */
    protected FlvTag readFlvTag(InputStream is) throws IOException {
        FlvTag ft = new FlvTag();

        // TagType (UInt8)
        is.read(ft.TagType);
        // DataSize (UInt24)
        is.read(ft.DataSize);
        // Timestamp (UInt24)
        is.read(ft.Timestamp);
        // TimestampExtended (UInt8)
        is.read(ft.TimestampExtended);
        // StreamID (UInt24)
        is.read(ft.StreamID);
        // Data
        int TagType = byteArrayToInt(ft.TagType);
        if (TagType == 8) {
            ft.Data = "AUDIODATA";
        } else if (TagType == 9) {
            ft.Data = "VIDEODATA";
        } else if (TagType == 18) {
            ft.Data = "SCRIPTDATAOBJECT";
        } else {
            ft.Data = null;
        }

        return ft;
    }

    /**
     * Parse meta data from video file.
     */
    protected void readMetaData() {
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(this.path));
            //// FLV-Header ////
            // check first 3 bytes ('F', 'L', 'V')
            byte byte1 = dis.readByte();
            byte byte2 = dis.readByte();
            byte byte3 = dis.readByte();
            if (byte1 != 'F' || byte2 != 'L' || byte3 != 'V') {
                System.out.println("no flv file. first 3 byte=" +
                        Integer.toHexString(byte1) +
                        Integer.toHexString(byte2) +
                        Integer.toHexString(byte3));
                return;
            }

            // FlashVideo-Version (UI8)
            this.Version = dis.readUnsignedByte();

            // Skip 5 bytes (TypeFlagsReserved UB[5] - Must be 0)
            dis.skip(5);

            // TypeFlagsAudio (UI8)
            this.TypeFlagsAudio = dis.readUnsignedByte();

            // Skip 1 byte (TypeFlagsReserved UB[1] - Must be 0)
            dis.skip(1);

            // TypeFlagsVideo (UB[1])
            this.TypeFlagsVideo = dis.readUnsignedByte();

            // DataOffset (UI32)
            this.DataOffset[0] = dis.readUnsignedByte();
            this.DataOffset[1] = dis.readUnsignedByte();
            this.DataOffset[2] = dis.readUnsignedByte();
            this.DataOffset[3] = dis.readUnsignedByte();
            //this.DataOffsetInt = dis.readUnsignedShort();

            //// FLV-Body ////
            // Skip 4 bytes (PreviousTagSize0 (UI32) - Always 0)
            dis.skip(4);
            // Tag1 (FLVTAG)
            this.Tag1 = this.readFlvTag(dis);
            // PreviousTagSize1 (UI32)
            dis.read(PreviousTagSize1);
            // Tag2 (FLVTAG)
            this.Tag2 = this.readFlvTag(dis);
            // PreviousTagSizeN-1 (UI32)
            dis.read(PreviousTagSizeN_1);
            // TagN (FLVTAG)
            this.TagN = this.readFlvTag(dis);
            // PreviousTagSizeN (UI32)
            dis.read(this.PreviousTagSizeN);

            //// AUDIODATA ////
            // SoundFormat (UB[4])
            dis.read(this.SoundFormat);

            dis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Structure for FLVTAG data.
     */
    class FlvTag {
        public byte[] TagType = new byte[1];
        public byte[] DataSize = new byte[3];
        public byte[] Timestamp = new byte[3];
        public byte[] TimestampExtended = new byte[1];
        public byte[] StreamID = new byte[3];
        /**
         * Body of the tag *
         */
        public String Data;
    }
}