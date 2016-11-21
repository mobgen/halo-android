package com.mobgen.halo.android.sdk.core.management.segmentation;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;

/**
 * Tag that collects the device support for NFC. Support does not means the NFC is connected for
 * the provided adapter.
 */
public class NFCSupportCollector implements TagCollector {

    @Override
    public HaloSegmentationTag collect(Context context) {
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        boolean hasNFC = adapter != null;
        return HaloSegmentationTag.createDeviceTag("NFC Support", hasNFC);
    }
}
