package com.example.version1;

public class ImageData {

        public byte[] data; // byte array for image data

        public ImageData() {
            // Default constructor required for Firestore
        }

        public ImageData(byte[] data) {
            this.data = data;
        }


}
