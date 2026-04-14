# MongoDB as a Vector Database for AI Agents

Modern AI systems are evolving rapidly. Large Language Models (LLMs) have become the backbone of many applications, but they come with a key limitation — **they are stateless**.

They don’t remember past interactions or learn from previous outcomes.

This limitation has led to the rise of **AI agents** — systems that combine reasoning, memory, and tool execution to solve real-world problems.

In this project, we go beyond theory and build a **multi-agent travel replanning system** using:

- Java (Spring Boot)
- MongoDB Atlas
- Vector Search (MongoDB)
- Voyage AI (for embeddings)

---

##  Why MongoDB for AI Agents?

MongoDB acts as a **unified data layer** for building intelligent systems.

Instead of using separate systems for:
- operational data
- vector storage
- event tracking

MongoDB brings everything together:

-  Document storage (trip state)
-  Event logging (disruptions)
-  Vector search (semantic memory)
-  Decision tracking (agent reasoning)

### Key Benefits

- **Vector + Operational Data Together**
- **Hybrid Search (semantic + structured queries)**
- **Simplified Architecture**
- **Developer-friendly ecosystem**

---

##  Understanding the Problem

Let’s consider a real-world scenario:

> You are traveling from Toronto → New York → San Francisco.  
> Your flight is delayed by 3 hours.  
> You have an important meeting.  

A traditional system would say:

> “Your flight is delayed.”

But that’s not helpful.

What you need is:

> “Here’s a better plan.”

---

##  Multi-Agent Architecture

This system is composed of multiple agents, each responsible for a specific task:

- **Monitoring Agent** → detects disruptions  
- **Planner Agent** → orchestrates decisions  
- **Memory Agent** → retrieves past incidents using vector search  
- **Booking Agent** → generates alternative routes  
- **Budget Agent** → filters options by cost  
- **Preference Agent** → applies user preferences  

Each agent is simple individually.  
Together, they form a **coordinated intelligent system**.

---

##  MongoDB Data Model

We use four collections:

| Collection | Purpose |
|----------|--------|
| `trip_state` | Current trip details |
| `events` | Disruptions |
| `agent_decisions` | Reasoning trail |
| `incident_memory` | Vectorized past incidents |

---

##  Running the System

### 1️⃣ Create a Trip

```bash
curl -X POST http://localhost:8080/trip/create \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "traveler-001",
    "preferences": {
      "airlinePreference": "SkyJet",
      "avoidRedEye": true,
      "maxAdditionalBudget": 250
    }
  }'
````

### Response

```json
{
  "id": "69dd6111674d2228e4db4b25",
  "userId": "traveler-001",
  "status": "ON_TRACK"
}
```

 Trip stored in `trip_state`
 Status = ON_TRACK

---

### 2️⃣ Simulate a Disruption

```bash
curl -X POST http://localhost:8080/event/simulate-delay \
  -H "Content-Type: application/json" \
  -d '{
    "tripId": "69dd6111674d2228e4db4b25",
    "delayMinutes": 180,
    "severity": "HIGH"
  }'
```

### Response

```json
{
  "type": "FLIGHT_DELAY",
  "severity": "HIGH",
  "metadata": {
    "from": "JFK",
    "to": "SFO",
    "delayMinutes": 180
  }
}
```

 Event stored in `events`
 Trip marked as `DISRUPTED`

---

### 3️⃣ Trigger Multi-Agent Replanning

```bash
curl -X POST http://localhost:8080/plan/replan \
  -H "Content-Type: application/json" \
  -d '{
    "tripId": "69dd6111674d2228e4db4b25"
  }'
```

### Response

```json
{
  "status": "REPLANNED",
  "itinerary": [
    {
      "fromLocation": "JFK",
      "toLocation": "ORD"
    },
    {
      "fromLocation": "ORD",
      "toLocation": "SFO"
    }
  ]
}
```

 New itinerary generated
 Trip updated in MongoDB

---

## 🔍 What Happened Internally?

###  Step 1: Memory Agent (Vector Search)

The system asks:

> “Have we seen something like this before?”

MongoDB vector search retrieves:

```
"Rebook via Chicago"
```

---

###  Step 2: Booking Agent

Generates multiple options:

* JFK → ORD → SFO
* JFK → ATL → SFO
* JFK → BOS → SFO

Memory boosts Chicago route.

---

###  Step 3: Budget Agent

Filters expensive routes.

---

###  Step 4: Preference Agent

Applies user constraints:

* Avoid red-eye
* Prefer SkyJet

---

###  Step 5: Planner Agent

Selects the best route:

```
JFK → ORD → SFO
```

Updates:

* `trip_state`
* `agent_decisions`

---

##  Inspect Agent Decisions

```bash
curl http://localhost:8080/decisions
```

### Response (trimmed)

```json
[
  {
    "agentName": "MemoryAgent",
    "reasoning": "Rebook via Chicago"
  },
  {
    "agentName": "BookingAgent"
  },
  {
    "agentName": "BudgetAgent"
  },
  {
    "agentName": "PreferenceAgent"
  },
  {
    "agentName": "PlannerAgent"
  }
]
```

 Full reasoning trail stored in MongoDB

---

##  Fetch Final Trip

```bash
curl http://localhost:8080/trip/{id}
```

```json
{
  "status": "REPLANNED"
}
```

---

##  What This Demonstrates

This system showcases:

* Event-driven architecture
* Multi-agent coordination
* Vector-based semantic memory
* Explainable AI decisions

---

##  Why This Matters

AI systems are shifting toward **agentic architectures** where:

* Memory is critical
* Context drives decisions
* Systems must be explainable

MongoDB enables this by combining:

* Document database
* Vector search
* Event storage

All in a single platform.

---
