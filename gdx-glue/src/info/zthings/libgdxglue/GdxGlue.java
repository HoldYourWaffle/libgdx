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

package info.zthings.libgdxglue;

import java.awt.Graphics;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;

/** Environment class holding references to the {@link Application}, {@link Graphics}, {@link Audio}, {@link Files} and
 * {@link Input} instances. The references are held in public static fields which allows static access to all sub systems. Do not
 * use Graphics in a thread that is not the rendering thread.
 * <p>
 * This is normally a design faux pas but in this case is better than the alternatives.
 * @author mzechner */
public class GdxGlue {
	public static ApplicationGlue app;
	public static Audio audio;
	public static Input input;
	public static Files files;
	public static Net net;
}
