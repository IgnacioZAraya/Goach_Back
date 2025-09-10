# General Architecture
## Decoupled:
A Decoupled Architecture is a software development model and concept that fosters independence among the components of 
an application components and allowing them to evolve separately based on the business needs, minimizing 
their dependencies between each other.
### Why choose a Decoupled Architecture?
The main reason for choosing this model was it's two main core ideas: _Autonomy_ & _Self-Determination_, this main concept allows us more 
freedom for the entire development of the app. Here's a more detailed list of advantages that this model brings
through its implementation:
 - Reduces system complexity.
 - Parallel development.
 - Technology flexibility. 
 - Easier CI/CD implementation.
 - Simpler testing.
 - Robust infrastructure against errors.
 - Easier scaling.
 - Easier maintenance.
 - Independent deployment.
 - Component reusability.
# Back-End Architecture:
## Onion Architecture:
An Onion Architecture is a software architectural pattern that forces dependencies flow inward, separating low-level 
modules from high-level ones, but ensure both depend and follow abstractions, which helps with: 
implementation, maintenance and their independent evolution. Honoring its name, this architecture divides the app 
organization in layers based on abstraction, like so:
- Domain Layer: Handles the business logic with its rules, while also defining the main entities for the app.
- Application Layer: Handles main use cases, app services and acts as the intermediary between the _Domain_ 
_Layer_ and 
the _Infrastructure Layer_.
- Infrastructure Layer: Handles the app interaction with external technologies and dependencies for specific 
use cases.
- Presentation Layer: Handles the main user interaction and the interfaces they will use to do so, usually 
interacting with the Application Layer.
### Why choose an Onion Architecture?
Onion Architecture is rooted in the concept the SOLID principle of dependency inversion, which helps with the 
development in an ecosystem that allows main independence on each layer of the app. This main idea, makes 
every related layer completely flexible, because they depend directly on abstractions, not chaining them to any 
other module or predefined technology, leaving them open for a complete makeover if new business necessities 
demands to.