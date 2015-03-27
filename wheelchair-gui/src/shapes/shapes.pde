// Simple example to demonstrate how JShapes can be created and modified in response to mouse interaction.

JShape[] shapes;      // Collection of shapes to display.

// Creates a set of random shapes.
void setup()
{
  size(400,400);
  shapes = new JShape[100];

  for (int i=0; i<shapes.length; i++)
  {
    // Choose one of four randomly selected shapes.
    float randShape = random(1,100);
    if (randShape < 25)
    {
      shapes[i] = new JEllipse(random(1,width),random(1,height),random(20,40),random(20,40));
    }
    else if (randShape < 50)
    {
      shapes[i] = new JRectangle(random(1,width),random(1,height),random(10,30),random(10,30));
    }
    else if (randShape < 75)
    {
      // Create a horizontally-based isosceles triangle.
      float x1 = random(30,width);
      float y1 = random(30,height);
      float w = random(20,50);
      float h = random(20,50);    
      shapes[i] = new JTriangle(x1,y1,x1-w,y1,x1-w/2,y1-h);
    }
    else
    {
      // Create a star shaped polygon.
      float x = random(30,width);
      float y = random(30,height);
      float lng = random(10,30);
      float shrt = lng/3;
      float[] xCoords = new float[]{x-shrt,x,x+shrt,x+lng,x+shrt,x,x-shrt,x-lng};
      float[] yCoords = new float[]{y-shrt,y-lng,y-shrt,y,y+shrt,y+lng,y+shrt,y};
      shapes[i] = new JPolygon(xCoords,yCoords); 
    }
    
    // You need to add a shape listener to any object that you wish to respond to mouse events.
    shapes[i].addShapeListener(new ShapeListener());
  }
}

// Draw each of the shapes on screen.
void draw()
{
  noLoop();    // This stops the display from redrawing when there is no change.
  smooth();
  background(255);
  
  for (int i=0; i<shapes.length; i++)
  {
    // Call the draw() method of the shape to get it to display itself.
    shapes[i].draw();
  }
}

// ------------------------------------------------------------------------------

// This class is the one that contains all the mouse-shape interaction responses.
// Each of the methods in this class is equivalent to the 'onMouseEntered()', 
// 'onMouseReleased()' etc. found in JavaScript and other langauges.
// There is no need to include all five methods below in your own code if you do not
// wish to respond to all types of mouse event.
class ShapeListener extends JShapeAdapter
{
  // Highlights the shape when a mouse is over it.
  void shapeEntered(JShape shape)
  {
    loop();    // Need to restart drawing loop since there will be some change in display.
    shape.setFillColour(color(120,40,40));
    shape.setBorderWidth(2);
  }
  
  // Returns shape to its default appearance when mouse leaves it.
  void shapeExited(JShape shape)
  {
    loop();    // Need to restart drawing loop since there will be some change in display.
    shape.setDefaultAppearance();
  }
  
  // Provides extra highlighting of shape when mouse is pressed over shape.
  void shapePressed(JShape shape)
  {
    loop();    // Need to restart drawing loop since there will be some change in display.
    shape.setFillColour(color(40,120,40));
    shape.setBorderWidth(5);
  }
  
  // Returns shape to its highlight colour if mouse button has been released (but will still
  // be within the shape's boundary).
  void shapeReleased(JShape shape)
  {
    loop();    // Need to restart drawing loop since there will be some change in display.
    shape.setFillColour(color(120,40,40));
    shape.setBorderWidth(2);
  }
      
  // Moves the shape as it gets dragged by the mouse around the screen.
  void shapeDragged(JShape shape, float dx, float dy)
  {
    loop();    // Need to restart drawing loop since there will be some change in display.
    shape.move(dx,dy);
  }
}
