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

	public List<String> getAuthors() {
		return authors;
	}

	public List<Reference> getReferenceList() {
		return referenceList;
	}

	public RefList(List<String> authors, String title, List<Reference> referenceList) {
		this.title = title;
		this.authors = authors;
		this.referenceList = referenceList;
	}

	private List<Reference> referenceList;
}
