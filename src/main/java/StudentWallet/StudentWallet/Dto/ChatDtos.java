package StudentWallet.StudentWallet.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ChatDtos {
	public static class CreateRoomRequest {
		@NotNull
		@Size(min = 3, max = 80)
		public String name;
		@Size(max = 500)
		public String description;
		public boolean isPrivate;
		// optional when private
		public String password;
	}

	public static class JoinRoomRequest {
		public String password;
	}

	public static class PostMessageRequest {
		@NotNull
		@Size(min = 1, max = 1000)
		public String content;
	}

	public static class RoomSummary {
		public Long id;
		public String name;
		public boolean isPrivate;
		public String creatorUsername;
		public int memberCount;
		public RoomSummary(Long id, String name, boolean isPrivate, String creatorUsername, int memberCount) {
			this.id = id;
			this.name = name;
			this.isPrivate = isPrivate;
			this.creatorUsername = creatorUsername;
			this.memberCount = memberCount;
		}
	}
} 