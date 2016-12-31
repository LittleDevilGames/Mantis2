package com.aftershock.mantis.scene;

/*
 * Copyright 2016 Luke Diamond
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MGameObject extends Actor {

	volatile Body body;
	volatile Fixture fixture;
	volatile TextureRegion tex;
	volatile Sprite drawSprite;
	boolean flipX = false, flipY = false;
	float opacity = 1.0f;
	float rotOffset = 0.0f;

	/**
	 * Creates a game object.
	 * 
	 * @param world
	 *            World to use for physics.
	 * @param name
	 *            Name of the object.
	 * @param type
	 *            Physical type of the object.
	 * @param initialPos
	 *            Initial position of the object.
	 * @param initialSize
	 *            Initial size of the object.
	 * @param texture
	 *            The object's texture.
	 * @param circular
	 *            Whether or not to treat this object as a circle.
	 * @param rotate
	 *            Whether or not this object can rotate.
	 * @param cat
	 *            Category bit.
	 * @param group
	 *            Group bit.
	 * @param mask
	 *            Mask bit.
	 */
	public MGameObject(World world, String name, BodyType type, Vector2 initialPos, Vector2 initialSize,
			TextureRegion texture, boolean circular, boolean rotate, int cat, int group, int mask) {
		FixtureDef fDef = new FixtureDef();
		fDef.density = 1f;
		fDef.friction = 0.5f;
		fDef.restitution = 0.25f;

		fDef.filter.categoryBits = (short) cat;
		fDef.filter.groupIndex = (short) group;
		fDef.filter.maskBits = (short) mask;

		Shape shape;

		if (circular) {
			shape = new CircleShape();
			((CircleShape) shape).setRadius(initialSize.x);
		} else {
			shape = new PolygonShape();
			((PolygonShape) shape).setAsBox(initialSize.x, initialSize.y);
		}
		fDef.shape = shape;

		BodyDef bDef = new BodyDef();
		bDef.gravityScale = 5f;
		bDef.bullet = true;
		bDef.fixedRotation = !rotate;
		bDef.type = type;
		bDef.allowSleep = true;

		body = world.createBody(bDef);
		body.setUserData(name);
		body.setTransform(initialPos.x, initialPos.y, 0.0f);
		body.setSleepingAllowed(true);
		fixture = body.createFixture(fDef);

		tex = new TextureRegion(texture);
		drawSprite = new Sprite(tex);

		this.setPosition(body.getPosition().x, body.getPosition().y);
		this.setRotation((float) Math.toDegrees(body.getAngle()));
		this.setSize(initialSize.x, initialSize.y);

		drawSprite.setSize(this.getWidth(), this.getHeight());
	}

	/**
	 * Creates a game object.
	 * 
	 * @param world
	 *            World to use for physics.
	 * @param name
	 *            Name of the object.
	 * @param type
	 *            Physical type of the object.
	 * @param initialPos
	 *            Initial position of the object.
	 * @param initialSize
	 *            Initial size of the object.
	 * @param texture
	 *            The name of the object's texture.
	 * @param circular
	 *            Whether or not to treat this object as a circle.
	 * @param rotate
	 *            Whether or not this object can rotate.
	 * @param cat
	 *            Category bit.
	 * @param group
	 *            Group bit.
	 * @param mask
	 *            Mask bit.
	 */
	public MGameObject(World world, String name, BodyType type, Vector2 initialPos, Vector2 initialSize, String texture,
			boolean circular, boolean rotate, int cat, int group, int mask) {
		this(world, name, type, initialPos, initialSize, new TextureRegion(new Texture(texture)), circular, rotate, cat,
				group, mask);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		drawSprite.setPosition(this.getX(), this.getY());
		drawSprite.setOrigin(drawSprite.getWidth() / 2.0f, drawSprite.getHeight() / 2.0f);
		drawSprite.setFlip(flipX, flipY);
		drawSprite.setScale(2.0f);
		drawSprite.setRotation(this.getRotation());
		drawSprite.setAlpha(opacity);
		drawSprite.draw(batch);
	}

	/**
	 * Set the texture of this object.
	 * 
	 * @param reg
	 *            New TextureRegion to use.
	 */
	public void setTex(TextureRegion reg) {
		tex = reg;
		Vector2 oldSize = new Vector2(drawSprite.getWidth(), drawSprite.getHeight());
		drawSprite = new Sprite(tex);
		drawSprite.setSize(oldSize.x, oldSize.y);
	}

	/**
	 * Sets the rotation offset of this object.
	 * 
	 * @param rot
	 *            The new sprite rotation offset.
	 */
	public void setSpriteRotOffset(float rot) {
		rotOffset = rot;
	}

	/**
	 * Gets the rotation offset of this object.
	 * 
	 * @return The sprite rotation offset.
	 */
	public float getSpriteRotOffset() {
		return rotOffset;
	}

	/**
	 * Update this object.
	 */
	public void update() {
		this.setPosition(
				body.getPosition().x - (drawSprite.getWidth() / 2.0f),
				body.getPosition().y - (drawSprite.getHeight() / 2.0f)
				);

		this.setRotation((float) Math.toDegrees(body.getAngle()) + rotOffset);
	}

}
