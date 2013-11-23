package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class PostSession {
	@SerializedName("title")
	private String title;
	@SerializedName("crowd_title")
	private String crowdTitle;
	@SerializedName("crowd_members")
	private String[] crowdMembers;
	@SerializedName("crowd")
	private int crowd;
	@SerializedName("use_existing_crowd")
	private boolean useExistingCrowd;
	
	public PostSession() {
		this.title = null;
		this.useExistingCrowd = false;
		this.crowdTitle = null;
		this.crowdMembers = null;
		this.crowd = -1;
	}

	public PostSession(String title, boolean useExistingCrowd, String crowdTitle, String[] crowdMembers,
			int crowdId) {
		this.title = title;
		this.crowdTitle = crowdTitle;
		this.crowdMembers = crowdMembers;
		this.crowd = crowdId;
		this.useExistingCrowd = useExistingCrowd;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCrowdTitle(String crowdTitle) {
		this.crowdTitle = crowdTitle;
	}

	public void setCrowdMembers(String[] crowdMembers) {
		this.crowdMembers = crowdMembers;
	}

	public void setCrowdId(int crowdId) {
		this.crowd = crowdId;
	}

	public void setUseExistingCrowd(boolean useExistingCrowd) {
		this.useExistingCrowd = useExistingCrowd;
	}
	
	
}
