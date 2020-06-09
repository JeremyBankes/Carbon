package com.jeremy.carbon;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TransferableImage implements Transferable {

	private DataFlavor[] transferDataFlavors;
	private Image image;

	public TransferableImage(Image image) {
		this.image = image;
		this.transferDataFlavors = new DataFlavor[] { DataFlavor.imageFlavor };
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return transferDataFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for (DataFlavor supportedFlavor : getTransferDataFlavors()) if (flavor == supportedFlavor) return true;
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (!isDataFlavorSupported(flavor)) return null;
		return image;
	}

}
