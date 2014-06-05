package reference;

import java.util.List;

public class RefList {
	private String title;
	private List<String> authors;

	public String getAuth_old() {
		return auth_old;
	}

	public void setAuth_old(String auth_old) {
		this.auth_old = auth_old;
	}

	private String auth_old;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public List<Reference> getReferenceList() {
		return referenceList;
	}

	public void setReferenceList(List<Reference> referenceList) {
		this.referenceList = referenceList;
	}

	public RefList(List<String> authors, String title, List<Reference> referenceList) {

		this.title = title;
		this.authors = authors;
		this.referenceList = referenceList;
	}

	private List<Reference> referenceList;
}
