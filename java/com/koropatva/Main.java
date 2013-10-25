package com.koropatva;

import com.koropatva.blogic.services.MainWorker;

public class Main {

	public static void main(String[] args) throws Exception {
		MainWorker mainWorker = new MainWorker();
		mainWorker.parsing(args);
	}

}
