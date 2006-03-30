package com.mysticcoders.pastebin.util;

import java.util.Iterator;

import wicket.model.IModel;

/**
 * Iterator adapter that wraps adaptee's elements with a model
 * 
 * @author Igor Vaynberg (ivaynberg)
 *
 */
public abstract class ModelIteratorAdapter implements Iterator {
	private Iterator delegate;

	public ModelIteratorAdapter(Iterator delegate) {
		this.delegate=delegate;
	}
	
	public boolean hasNext() {
		return delegate.hasNext();
	}

	public Object next() {
		return model(delegate.next());
	}

	public void remove() {
		delegate.remove();
	}
	
	abstract protected IModel model(Object object);
}
