package edu.hust.dependency;

import java.io.IOException;

public class DependencySampleStream {//extends FilterObjectStream<String, DependencySample>
	public DependencySample getDepSample(String sentence) {
		return new DependencySample();
	}
	public DependencySample read() throws IOException{
		return new DependencySample();
	}
}
