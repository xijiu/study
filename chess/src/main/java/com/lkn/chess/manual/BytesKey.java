package com.lkn.chess.manual;

import java.util.Arrays;

public class BytesKey {
    private byte[] array;

    public BytesKey(byte[] array) {
        this.array = array;
    }

    public byte[] getArray() {
        return array.clone();
    }

    public BytesKey setArray(byte[] array) {
        this.array = array;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        BytesKey bytesKey = (BytesKey) o;
        return Arrays.equals(array, bytesKey.array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }
}