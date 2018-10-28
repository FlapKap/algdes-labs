#include <stdlib.h>
#include <stdio.h>
#include <limits.h>

#pragma region IntOption

typedef enum _IntOptionType
{
    NONE,
    SOME
} IntOptionType;

typedef struct _IntOption
{
    IntOptionType type;
    int value;
} IntOption;

IntOption none()
{
    return (IntOption) { .type = NONE };
}

IntOption some(int value)
{
    return (IntOption) { .type = SOME, .value = value };
}

IntOption opt_map(IntOption opt, IntOption (*optFunc)(int))
{
    if (opt.type == NONE)
    {
        return none();
    }
    return (*optFunc)(opt.value);
}

int force(IntOption opt)
{
    return opt.value;
}

#pragma endregion IntOption

#pragma region DEPRECATED

// #pragma region IntPair

// typedef struct _IntPair
// {
//     int left;
//     int right;
// } IntPair;

// IntPair* pair_h(int left, int right)
// {
//     IntPair* pair = malloc(sizeof(IntPair));
//     pair->left = left;
//     pair->right = right;
//     return pair;
// }

// IntPair pair_s(int left, int right)
// {
//     return (IntPair) { .left = left, .right = right };
// }

// #pragma endregion IntPair

// #pragma region IntTriplet

// typedef struct _IntTriplet {
//     int left;
//     int center;
//     int right;
// } IntTriplet;

// IntTriplet* triplet_h(int left, int center, int right)
// {
//     IntTriplet* t = malloc(sizeof(IntTriplet));
//     t->left = left;
//     t->center = center;
//     t->right = right;
//     return t;
// }

// IntTriplet triplet_s(int left, int center, int right)
// {
//     return (IntTriplet) { .left = left, .center = center, .right = right };
// }

// #pragma endregion IntTriplet

#pragma endregion DEPRECATED

#pragma region IntStream

struct _IntStream;

typedef struct _Cons
{
    int head;
    struct _IntStream* tail;
} Cons;

typedef enum _IntStreamType
{
    CONS,
    NIL
} IntStreamType;

typedef struct _IntStream 
{
    IntStreamType type;
    Cons* cons;
} IntStream;

IntStream* _nil;
IntStream* nil()
{
    if (_nil)
    {
        return _nil;
    }
    _nil = malloc(sizeof(IntStream));
    _nil->type = NIL;
    return _nil;
}

IntStream* cons(int head, IntStream* tail) 
{
    IntStream* result = malloc(sizeof(IntStream));
    result->type = CONS;
    result->cons = malloc(sizeof(Cons));
    result->cons->head = head;
    result->cons->tail = tail;
    return result;
}

IntOption head_opt(IntStream* s)
{
    if (s->type == NIL)
    {
        return none();
    }
    return some(s->cons->head);
}

int head(IntStream* s)
{
    return force(head_opt(s));
}

IntStream* tail(IntStream* s)
{
    if (s->type == NIL) 
    {
        return nil();
    }
    return s->cons->tail;
}

IntStream* map_to_int(IntStream* s, int (*mapper) (int))
{
    if (s->type == NIL) 
    {
        return s;
    } 
    return cons((*mapper)(head(s)), map_to_int(tail(s), mapper));
}

IntStream* reverse_rec(IntStream* acc, IntStream* s)
{
    if (s->type == NIL)
    {
        return acc;
    }
    return reverse_rec(cons(head(s), acc), tail(s));
}

IntStream* reverse(IntStream* s)
{
    return reverse_rec(nil(), s);
}

IntStream* clone(IntStream* s)
{
    return reverse(reverse(s));
}

#pragma region DEPRECATED

IntStream* concat_rec(IntStream* acc, IntStream* s1, IntStream* s2) 
{
    if (s2->type == NIL)
    {
        return reverse(acc);
    }
    if (s1->type == NIL)
    {
        return concat_rec(cons(head(s2), acc), nil(), tail(s2));
    }
    return concat_rec(cons(head(s1), acc), tail(s1), s2);
}

IntStream* concat(IntStream* s1, IntStream* s2)
{
    return concat_rec(nil(), s1, s2);
}

IntStream* append(IntStream* s, int i)
{
    return reverse(cons(i, reverse(s)));
}

// IntStream* flatmap(IntStream* s, IntStream* (*mapper)(int))
// {
//     if (s->type == NIL)
//     {
//         return s;
//     }
//     IntStream* mapped = (*mapper)(s->cons->head);
//     return flatmap(concat(s, mapped), mapper);
// }

// IntStream* filter_rec(IntStream* acc, IntStream* s, int (*predicate)(int))
// {
//     if (s->type == NIL)
//     {
//         return reverse(acc);
//     }
//     int h = head(s);
//     if((*predicate)(h))
//     {
//         return filter_rec(cons(h, acc), tail(s), predicate);
//     }
//     return filter_rec(acc, tail(s), predicate);
// }

// IntStream* filter(IntStream* s, int (*predicate)(int))
// {
//     return filter_rec(nil(), s, predicate);
// }

#pragma endregion DEPRECATED

int contains(IntStream* s, int to_check)
{
    if (s->type == NIL)
    {
        return 0;
    }
    if(head(s) == to_check) {
        return 1;
    }
    return contains(tail(s), to_check);
}

void intStream_destroy(IntStream* s)
{
    if (s->type == NIL)
    {
        return;
    } else {
        IntStream* t = tail(s);
        free(s);
        intStream_destroy(t);
    }
}

void intStream_print_rec(IntStream* s)
{
    if(s->type == NIL)
    {
        printf("]\n");
        return;
    }
    printf(", %d", head(s));
    return intStream_print_rec(tail(s));
}

void intStream_print(IntStream* s)
{
    if (s->type == NIL)
    {
        printf("[]\n");
        return;
    }
    printf("[ %d", head(s));
    return intStream_print_rec(tail(s));
}

#pragma endregion IntStream

#pragma region Path

typedef struct _Path {
    IntStream* nodes;
    int bottleneck;
} Path;

Path* mk_path(IntStream* nodes, int bottleneck)
{
    Path* path = malloc(sizeof(Path));
    path->nodes = nodes;
    path->bottleneck = bottleneck;
    return path;
}

void path_destroy(Path* path)
{
    intStream_destroy(path->nodes);
    free(path);
}

#pragma endregion Path

#pragma region Graph

typedef struct _Graph {
    int size;
    int sink;
    int** graph;
} Graph;

Graph* mk_graph(int size)
{
    int** graph = malloc(sizeof(int*) * size);
    int u;
    for (u = 0; u < size; u++)
    {
        graph[u] = malloc(sizeof(int));
    }
    Graph* toReturn = malloc(sizeof(Graph));
    toReturn->size = size;
    toReturn->sink = size-1;
    toReturn->graph = graph;
    return toReturn;
}

Graph* clone_graph(Graph* g)
{
    Graph* clone = mk_graph(g->size);
    int i;
    int j;
    for (i = 0; i < g->size; i++)
    {
        for (j = 0; j < g->size; j++)
        {
            clone->graph[i][j] = g->graph[i][j];
        }
    }
    return clone;
}

void print_graph(Graph* g)
{
    int i;
    int j;
    for (i = 0; i < g->size; i++)
    {
        for (j = 0; j < g->size; j++)
        {
            printf("%03d ", g->graph[i][j]);
        }
        printf("\n");
    }
}

void put_edge(Graph* graph, int from, int theres, int cap)
{
    graph->graph[from][theres] = cap;
}

Path* find_path_rec(Graph* graph, Path* acc, int currentNode)
{
    if(currentNode == graph->sink)
    {
        return mk_path(append(acc->nodes, currentNode), acc->bottleneck);
    }

    IntStream* adj = nil();
    int u;
    for(u = 0; u < graph->size; u++)
    {
        if (!contains(acc->nodes, u) && graph->graph[currentNode][u] > 0)
        {
            adj = cons(u, adj);
        }
    }
    while(adj->type != NIL)
    {
        int nextNode = head(adj);
        adj = tail(adj);
        if (contains(acc->nodes, nextNode))
        {
            continue;
        }
        int cap = graph->graph[currentNode][nextNode];
        int currentMin = (cap < acc->bottleneck) ? cap : acc->bottleneck;
        Path* next = mk_path(append(acc->nodes, currentNode), currentMin);
        Path* path = find_path_rec(graph, next, nextNode);
        if (!contains(path->nodes, graph->sink))
        {
            path_destroy(path);
            continue;
        }
        return path;
    }
    return acc;
}

Path* find_path(Graph* graph)
{
    return find_path_rec(graph, mk_path(nil(), INT_MAX), 0);
}

void augment_by_path_rec(Graph* g, IntStream* nodes, int bottleneck)
{
    IntStream* t = tail(nodes);
    if (nodes->type == NIL || tail(nodes)->type == NIL)
    {
        return;
    }
    int v = head(nodes);
    int u = head(t);
    g->graph[v][u] = g->graph[v][u] - bottleneck;
    g->graph[u][v] = g->graph[u][v] + bottleneck;
    return augment_by_path_rec(g, t, bottleneck);
}

void augment_by_path(Graph* g, Path* p)
{
    return augment_by_path_rec(g, p->nodes, p->bottleneck);
}

void print_source_component(Graph* residual, Graph* original)
{
    int* visited = malloc(sizeof(int) * residual->size);
    int v_idx;
    for(v_idx = 0; v_idx < residual->size; v_idx++)
    {
        visited[v_idx] = 0;
    }
    IntStream* component = cons(0, nil());
    IntStream* queue = cons(0, nil());
    while(queue->type != NIL)
    {
        int v = head(queue);
        visited[v] = 1;
        int u;
        for(u = 0; u < residual->size; u++)
        {
            if (!visited[u] && residual->graph[v][u] != 0)
            {
                component = cons(u, component);
                queue = cons(u, tail(queue));
            }
            else
            {
                queue = tail(queue);
            }
        }
    }
    intStream_destroy(queue);
    IntStream* source_component = clone(component);
    while(source_component->type != NIL)
    {
        int i = head(source_component);
        source_component = tail(source_component);
        int j;
        for(j = 0; j < residual->size; j++)
        {
            if(!contains(component, j) && original->graph[i][j] != 0)
            {
                printf("%d %d %d\n", i, j, original->graph[i][j]);
            }
        }
    }
}

#pragma endregion Graph

#pragma region MinCut

int max_flow(Graph* g, int currentFlow)
{
    Path* path = find_path(g);

    if (path->nodes->type == NIL || !contains(path->nodes, g->sink))
    {
        return currentFlow;
    }
    int bn = path->bottleneck;
    augment_by_path(g, path);

    path_destroy(path);
    return max_flow(g, bn + currentFlow);
}

void minimum_cut(Graph* g)
{
    Graph* r = clone_graph(g);
    int mf = max_flow(r, 0);
    printf("max-flow: %d\n", mf);
    print_source_component(r, g);
}

#pragma endregion MinCut

#pragma region Parser

// Write parser here.

#pragma endregion Parser

int main(int argc, char const *argv[])
{
    // Simple graph:
    // Graph* g = mk_graph(5);
    // int source = 0;
    // int sink = 4;
    // g->graph[0][1] = 10;
    // g->graph[1][2] = 10;
    // g->graph[2][sink] = 5;
    // g->graph[source][3] = 8;
    // g->graph[3][sink] = 8;

    // Graph from slides:
    Graph* g = mk_graph(6);
    int s = 0;
    int t = 5;
    g->graph[s][1] = 10;
    g->graph[s][2] = 10;
    g->graph[1][2] = 2;
    g->graph[1][3] = 4;
    g->graph[1][4] = 8;
    g->graph[2][4] = 9;
    g->graph[4][3] = 6;
    g->graph[3][t] = 10;
    g->graph[4][t] = 10;

    minimum_cut(g);
    return 0;
}
