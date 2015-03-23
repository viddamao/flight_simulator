# flightsim
A basic flight simulator over generated terrain.

Refine each quad of the initial mesh using the diamond-square algorithm, using Perlin noise for the random perturbations. Note that this is also a data structure problem, as you'll need to choose/design a good data structure to maintain the mesh.

Approximate normals for each face and vertex of the resulting [refined] mesh. Newell's method gives a way to approximate normals for not-necessarily-planar polygons. The faces in your mesh will fall into this category unless you triangulate (triangles are always planar), so use Newell's method. For vertex normals, the common technique is to normalize the sum of normals from adjoining faces.

Implement level of detail: only perform refinement on render finer terrain close to the camera. 
We used different method which is calculate the distance,and if greater than certain value we render 1/4 of the vertices instead of all of them which gives us a less refined rendering.

Surround the scene with a skybox. The user shouldn't be allowed to fly outside of the skybox (in other words, do collision detection). This is easier to program if you make your skybox axis aligned. You can use techniques (e.g. a plane for water) to connect the terrain to the skybox. A sample skybox texture has been provided on Github.

We created the Skybox but found it difficult to fit it into the save frame.

Color/texture the terrain by height. Feel free to use fog or other techniques to make distant terrain less distinct.
Added fog feature.

Implement the following user controls:

Key "[", "]" and left right arrow:change the flight direction (bank and tilt)
Up and down arrow controls speed up and slow down flight speed
Keyboard A: switch between a smooth shaded, flat shaded, and wireframe terrain
Keyboard R: reset the scene viewpoint
Keyboard Q: pop message and then quit the program
