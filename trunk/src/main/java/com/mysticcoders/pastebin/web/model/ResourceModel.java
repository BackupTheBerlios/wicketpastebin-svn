package com.mysticcoders.pastebin.web.model;

import wicket.Component;
import wicket.model.AbstractReadOnlyModel;

/**
 * A model whose value is a localized resource with the specified key
 *
 * @author igor
 *
 */
public class ResourceModel extends AbstractReadOnlyModel {
	/**
	 * resource key of the resource this model points to
	 */
	private String resourceKey;

	/**
	 * Constructor
	 *
	 * @param resourceKey
	 *            resource key of the resource this model points ot
	 */
	public ResourceModel(final String resourceKey) {
		if (resourceKey == null) {
			throw new IllegalArgumentException(
					"[resourceKey] argument cannot be null");
		}
		this.resourceKey = resourceKey;
	}

	/**
	 * Resolve the specified resource key to the resource using a localizer
	 * @see wicket.model.AbstractReadOnlyModel#getObject(wicket.Component)
	 */
	@Override
	public Object getObject(Component component) {
		if (component==null) {
			throw new IllegalArgumentException("getObject() cannot be called on a ResourceModel with a null [component] argument.");
		}
		return component.getLocalizer().getString(resourceKey, component);
	}

}