/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.tests.extensions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class FreeTypeIncrementalTest extends GdxTest {
	SpriteBatch batch;
	ShapeRenderer shapes;
	BitmapFont font;
	FreeTypeFontGenerator generator;

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapes = new ShapeRenderer();
		shapes.setColor(Color.RED);

		FreeTypeFontGenerator.setMaxTextureSize(128);

		generator = new FreeTypeFontGenerator(Gdx.files.internal("data/arial.ttf"));

		FreeTypeFontParameter param = new FreeTypeFontParameter();
		param.incremental = true;
		param.size = 24;
		param.characters = "howdY\u0000";

		FreeTypeBitmapFontData data = new FreeTypeBitmapFontData() {
			@Override
			public int getWrapIndex (Array<Glyph> glyphs, int start) {
				return SimplifiedChinese.getWrapIndex(glyphs, start);
			}
		};

		// By default latin chars are used for x and cap height, causing some fonts to display non-latin chars out of bounds.
		data.xChars = new char[] {'åŠ¨'};
		data.capChars = new char[] {'åŠ¨'};

		font = generator.generateFont(param, data);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Draw rects.
		shapes.begin(ShapeType.Line);
		float x = 0, y = Gdx.graphics.getHeight() - font.getRegion().getRegionHeight() - 1;
		for (int i = 0, n = font.getRegions().size; i < n; i++) {
			TextureRegion region = font.getRegions().get(i);
			shapes.rect(x, y, region.getRegionWidth(), region.getRegionHeight());
			x += region.getRegionWidth() + 2;
		}
		shapes.rect(10, 250, Gdx.graphics.getWidth() - 20, -240);
		shapes.end();

		batch.begin();
		x = 0;
		for (int i = 0, n = font.getRegions().size; i < n; i++) {
			TextureRegion region = font.getRegions().get(i);
			batch.draw(region, x, y);
			x += region.getRegionWidth() + 2;
		}
		font.draw(batch, "LYA", 10, 300); // Shows kerning.
		font.draw(batch, "hello world", 100, 300);
		font.draw(batch,
			"åŠ¨ç”»èƒ½ç»™æ¸¸æˆ�å¸¦æ�¥ç”Ÿæœºå’Œç�µæ°”ã€‚æˆ‘ä»¬ç›¸ä¿¡åˆ›ä½œä¸€æ®µç¾Žå¦™çš„åŠ¨ç”»ï¼Œä¸�ä»…éœ€è¦�å¼ºå¤§çš„è½¯ä»¶å·¥å…·ï¼Œæ›´éœ€è¦�ä¸€å¥—ç‰› B çš„å·¥ä½œæµ�ç¨‹ã€‚" //
				+ "Spineä¸“æ³¨äºŽæ­¤ï¼Œä¸ºæ‚¨åˆ›å»ºæƒŠè‰³çš„éª¨éª¼åŠ¨ç”»ï¼Œå¹¶å°†å…¶æ•´å�ˆåˆ°æ¸¸æˆ�å½“ä¸­ï¼Œæ��ä¾›äº†ä¸€å¥—é«˜æ•ˆçš„å·¥ä½œæµ�ç¨‹ã€‚",
			10, 250, //
			Gdx.graphics.getWidth() - 20, Align.left, true);
		batch.end();
	}

	@Override
	public void resize (int width, int height) {
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		shapes.setProjectionMatrix(batch.getProjectionMatrix());
	}

	static public class SimplifiedChinese {
		public static int getWrapIndex (Array<Glyph> glyphs, int start) {
			int i = start - 1;
			for (; i >= 1; i--) {
				int startChar = glyphs.get(i).id;
				if (!SimplifiedChinese.legalAtStart(startChar)) continue;
				int endChar = glyphs.get(i - 1).id;
				if (!SimplifiedChinese.legalAtEnd(endChar)) continue;
				// Don't wrap between ASCII chars.
				if (startChar < 127 && endChar < 127 && !Character.isWhitespace(startChar)) continue;
				return i;
			}
			return start;
		}

		static private boolean legalAtStart (int ch) {
			switch (ch) {
			case '!':
			case '%':
			case ')':
			case ',':
			case '.':
			case ':':
			case ';':
			case '>':
			case '?':
			case ']':
			case '}':
			case 'Â¢':
			case 'Â¨':
			case 'Â°':
			case 'Â·':
			case 'Ë‡':
			case 'Ë‰':
			case 'â€•':
			case 'â€–':
			case 'â€™':
			case 'â€�':
			case 'â€ž':
			case 'â€Ÿ':
			case 'â€ ':
			case 'â€¡':
			case 'â€º':
			case 'â„ƒ':
			case 'âˆ¶':
			case 'ã€�':
			case 'ã€‚':
			case 'ã€ƒ':
			case 'ã€†':
			case 'ã€ˆ':
			case 'ã€Š':
			case 'ã€Œ':
			case 'ã€Ž':
			case 'ã€•':
			case 'ã€—':
			case 'ã€ž':
			case 'ï¹˜':
			case 'ï¹š':
			case 'ï¹œ':
			case 'ï¼�':
			case 'ï¼‚':
			case 'ï¼…':
			case 'ï¼‡':
			case 'ï¼‰':
			case 'ï¼Œ':
			case 'ï¼Ž':
			case 'ï¼š':
			case 'ï¼›':
			case 'ï¼Ÿ':
			case 'ï¼½':
			case 'ï½€':
			case 'ï½œ':
			case 'ï½�':
			case 'ï½ž':
				return false;
			}
			return true;
		}

		static private boolean legalAtEnd (int ch) {
			switch (ch) {
			case '$':
			case '(':
			case '*':
			case ',':
			case 'Â£':
			case 'Â¥':
			case 'Â·':
			case 'â€˜':
			case 'â€œ':
			case 'ã€ˆ':
			case 'ã€Š':
			case 'ã€Œ':
			case 'ã€Ž':
			case 'ã€�':
			case 'ã€”':
			case 'ã€–':
			case 'ã€�':
			case 'ï¹—':
			case 'ï¹™':
			case 'ï¹›':
			case 'ï¼„':
			case 'ï¼ˆ':
			case 'ï¼Ž':
			case 'ï¼»':
			case 'ï½›':
			case 'ï¿¡':
			case 'ï¿¥':
				return false;
			}
			return true;
		}
	}
}
