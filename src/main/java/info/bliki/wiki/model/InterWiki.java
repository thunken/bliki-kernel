package info.bliki.wiki.model;

import lombok.Getter;

@Getter
public class InterWiki {
	final String pattern;
	final boolean local;

	InterWiki(final String pattern, final boolean local) {
		this.pattern = pattern;
		this.local = local;
	}
}
