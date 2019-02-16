package net.ncguy.pmx.mmd;

import net.ncguy.pmx.mmd.data.Face;
import net.ncguy.pmx.mmd.data.Header;
import net.ncguy.pmx.mmd.data.Material;
import net.ncguy.pmx.mmd.data.Vertex;

public class Pmx {
	private Header header;
	private Vertex[] vertices;
	private Face[] faces;
	private String[] textures;
	private Material[] materials;

	public Material[] getMaterials() {
		return materials;
	}

	public void setMaterials(Material[] materials) {
		this.materials = materials;
	}

	public String[] getTextures() {
		return textures;
	}

	public void setTextures(String[] textures) {
		this.textures = textures;
	}

	public Face[] getFaces() {
		return faces;
	}

	public void setFaces(Face[] faces) {
		this.faces = faces;
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public Vertex[] getVertices() {
		return vertices;
	}

	public void setVertices(Vertex[] vertices) {
		this.vertices = vertices;
	}
}