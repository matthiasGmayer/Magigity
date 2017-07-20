package gameStates;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import debug.Debug;
import effects.ParticleEffect;
import enitities.Camera;
import enitities.Entity;
import enitities.Player;
import info.Collision;
import info.Information;
import terrain.Building;
//import shaders.EntityShader;
import tools.Loader;
import tools.States;
import tools.Toolbox;
public class Running extends BasicGameState{
	
	Loader loader = new Loader();
	Input input;
	ArrayList<Entity> entities = Entity.entities;
	ArrayList<Building> buildings = Building.buildings;
	Player player;
	Camera camera = new Camera();
	//EntityShader entityShader = new EntityShader();
	
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		Information.currentCamera = camera;
		input = new Input(Input.ANY_CONTROLLER);
		player = new Player(new Circle(0,0,25),new Rectangle(0,0,75, 25), new Vector2f(1,1), 0, 1);
		for(int i = 1; i < 10; i++){
			new ParticleEffect("torch", new Entity(Loader.loadImage("BlackCircle"), new Circle(100*i,100*i,25), new Vector2f(1f, 0.5f), 0, 1), 1000);
		}
		new ParticleEffect("torch",player, 1000000);
	}
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		for(int i = 0; i < entities.size()-1; i++)
		{
			for(int j = i+1; j < entities.size(); j++)
			entities.get(i).collide(entities.get(j));
		}
		for(int i = 0; i < entities.size(); i++){
			for(int j = 0; j < buildings.size(); j++){
				entities.get(i).collide(buildings.get(j));
			}
		}
		
		player.update(input, camera, delta);
		for(Entity entity:entities){
			entity.update(delta);
		}
		
		
		
		camera.update(input, delta);
		if(Information.isMouseInactive()){
			camera.targetPosition.set(player.position);
		}else{
			camera.targetPosition.set(Toolbox.getLineDivision(player.position, 3, camera.getScreenToWorldPoint(Information.getMouse()), 1));
		}
		Toolbox.approachVector(camera.position, camera.targetPosition, delta);
		Toolbox.approachVector(camera.size, camera.zoom, delta);
		camera.setRotationRadians(Toolbox.approachValue(camera.getRotationRadians(), camera.getTargetRotationRadians(), delta));
		Information.update(delta);
		
		}
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		
		//ONSCREEN
//		g.setAntiAlias(false);
		g.setColor(Color.green);
		g.fillRect(0, 0, Display.getWidth(), Display.getHeight());		
		
		
		
		
		
		
		//ONWORLD
		Vector2f translation = camera.getWorldToScreenPoint(new Vector2f(0, 0));
		g.translate(translation.x, translation.y);
		g.rotate(0, 0, camera.getRotationDegrees());
		g.scale(camera.size.x, camera.size.y);
		//entityShader.start();
		for(Entity entity : entities){
			entity.render(g);
		}
		for(Building building : buildings){
//			building.render(g);
		}
		
		
		//entityShader.stop();

		//Debug
		g.setColor(Color.red);
		for(Vector2f point : Debug.debugPoints){
			Debug.setDebugPoint(point.x, point.y);
			g.draw(Debug.debugPoint);
			}
		Debug.debugPoints.clear();
		g.resetTransform();
	}
	@Override
	public void keyPressed(int key, char c) {
		if(key == Input.KEY_LEFT)
			camera.addToTargetRotationDegrees(-90);
		if(key == Input.KEY_RIGHT)
			camera.addToTargetRotationDegrees(90);
		super.keyPressed(key, c);
	}
	
	@Override
	public int getID() {
		return States.running;
	}
}

