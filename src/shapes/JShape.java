package shapes;

import processing.core.PApplet;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/** Represents any shape that can be drawn on screen and inform listeners that it 
  * has interacted with the mouse. Implementations should subclass this to provide
  * the geometry, drawing and movement code.
  * @version 1.0, 23rd July, 2008.
  * @author Jo Wood.
  */
public abstract class JShape
{
  // ------------------- Object variables ------------------
  
                    /** Java2D representation of the shape. */
  protected Shape shape;
                    /** Colour of the inside of the shape. */
  protected int fillColour;
                    /** Colour of the boundary of the shape. */
  protected int borderColour;
                    /** Width of the boundary of the shape. */
  protected float borderWidth;
    /** Reference to the Canvas on which we want to draw **/
  protected PApplet parent;

  private ArrayList shapeListeners;
  private boolean isInside,isMouseDown;
  private int oldMouseX,oldMouseY;
  
  private static final int   DEFAULT_FILL_COLOUR = 240;
  private static final int   DEFAULT_BORDER_COLOUR = 80;
  private static final float DEFAULT_BORDER_WIDTH = 0.5f;
    
  // -------------------- Constructors --------------------
  
  /** Initialises the shape. This should be called by all subclasses
    * before invoking their specific initialisation.
    */
  public JShape(PApplet parent)
  {
      this.parent = parent;
    shapeListeners = new ArrayList();
    setDefaultAppearance(); 
    isInside = false;
    isMouseDown = false;
    parent.registerMouseEvent(this);
  }
  
  // ---------------------- Methods -----------------------
  
  /** Prepares the shape for drawing. This should be called by all subclasses
    * before invoking their specific drawing behaviour. 
    */
  public void draw()
  {
    parent.fill(fillColour);
    if(borderWidth == 0){
        parent.noStroke();
    } else {
        parent.strokeWeight(borderWidth);
        parent.stroke(borderColour);
    }
  }
  
  /** Should perform a translation of the object by the given coordinates.
    * @param dx Amount to move in the x-direction.
    * @param dy Amount to moce in the y-direction.
    */
  public abstract void move(float dx, float dy);
     
  /** Responds to mouse events by informing all shape listeners when the mouse
    * has entered, exited, been pressed, released or dragged over the shape.
    * @param e Mouse event.
    */
  public void mouseEvent(MouseEvent e)
  {
      int mouseX = e.getX();
      int mouseY = e.getY();

    // Once a mouse button is held down, testing for mouse release and dragging
    // is independent of whether mouse is inside or outside shape.
    if (e.getID() == MouseEvent.MOUSE_RELEASED)
    {
      isMouseDown = false;
    }
    
    if ((isMouseDown) && (e.getID() == MouseEvent.MOUSE_DRAGGED))
    {
      int mouseDx = mouseX-oldMouseX;
      int mouseDy = mouseY-oldMouseY;
      oldMouseX = mouseX;
      oldMouseY = mouseY;
      Iterator i = shapeListeners.iterator();
      while (i.hasNext())
      {
        ((JShapeListener)i.next()).shapeDragged(this,mouseDx,mouseDy);
      }
    }
     
    
    // Check whether mouse is currently inside shape.
    if (shape.contains(mouseX,mouseY))
    {     
      if (isInside == false)
      {
        // We have just entered the object.
        isInside = true;
        Iterator i = shapeListeners.iterator();
        while (i.hasNext())
        {
          ((JShapeListener)i.next()).shapeEntered(this);
        }
      }

      if (e.getID() == MouseEvent.MOUSE_PRESSED)
      {
          if (isMouseDown == false)
          {
            isMouseDown = true;
            oldMouseX = mouseX;
            oldMouseY = mouseY;
          }
          Iterator i = shapeListeners.iterator();
          while (i.hasNext())
          {
            ((JShapeListener)i.next()).shapePressed(this);
          }
      }   
      else if (e.getID() == MouseEvent.MOUSE_RELEASED)
      {
          Iterator i = shapeListeners.iterator();
          while (i.hasNext())
          {
            ((JShapeListener)i.next()).shapeReleased(this);
          }
      }      
    }
    else if (isInside == true)
    {
      // We have just exited the object.
      isInside = false;
      Iterator i = shapeListeners.iterator();
      while (i.hasNext())
      {
        ((JShapeListener)i.next()).shapeExited(this);
      } 
    } 
  }
    
  /** Adds a listener that will be informed when the shape interacts with the mouse.
    * @param listener Listener to be informed.
    */
  public void addShapeListener(JShapeListener listener)
  {
    shapeListeners.add(listener);
  }
  
  /** Removes a listener from the collection that will be informed when the shape interacts with the mouse.
    * @param listener Listener to be removed.
    * @return True if listener was successfully removed.
    */
  public boolean removeShapeListener(JShapeListener listener)
  {
    return shapeListeners.remove(listener);
  }
  
  /** Sets the colour of the inside of the shape.
    * @param colour Colour to use (created with Processing's <code>color()</code> method).
    */
  public void setFillColour(int colour)
  {
    this.fillColour = colour;
  }
  
  /** Sets the colour of the boundary of the shape.
    * @param colour Colour to use (created with Processing's <code>color()</code> method).
    */
  public void setBorderColour(int colour)
  {
    this.borderColour = colour;
  }
  
  /** Sets the border width of the shape.
    * @param borderWidth Width of border in pixels (can be fractions of a pixel).
    */
  public void setBorderWidth(int borderWidth)
  {
    this.borderWidth = borderWidth;
  }
    
  /** Resets the appearance of the shape to its default colours and border width.
    */
  void setDefaultAppearance()
  {
    fillColour   = parent.color(DEFAULT_FILL_COLOUR);
    borderColour = parent.color(DEFAULT_BORDER_COLOUR);
    borderWidth  = DEFAULT_BORDER_WIDTH; 
  }
}
