package kodkodmod.examples;

import java.text.Normalizer.Form;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.ast.operator.ExprOperator;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;

/**
 * A KodKod encoding of the Pacman game, modified such that
 * <ol>
 * <li>Pacman wins if it enters a field with a treasure</li>
 * <li>Pacman looses if it is hit by ghost; if Pacman enters a field with a
 * treasure AND a ghost, Pacman looses</li>
 * </ol>
 * 
 * To run the Pacman problem in the Alloy analyzer use the following code:
 * 
 * <p>
 * 
 * <pre>
 * // Field Sigs
 * 	abstract sig Field { neighbors : some Field }
 * one sig Field0, Field1, Field2 extends Field {}
 * one sig TreasureField extends Field {}
 * // Player Sigs
 * abstract sig Player { on : Field }
 * one sig Pacman extends Player {}{ on = Field0 }
 * sig Ghost extends Player {}{ on = Field1 or on = Field2 }
 * 
 * 
 * // Game Setup:
 * // F0 <-> F1
 * // ^      ^
 * // |      |
 * // v      v
 * // F2 <-> TF
 * fact {
 * 	neighbors = Field0->Field1 + Field0->Field2 + Field1->Field0 + Field1->TreasureField + 
 * 					Field2->Field0 + Field2->TreasureField + TreasureField->Field1 + TreasureField->Field2
 * }
 * 
 * // Transition Relation - required to be left-total!
 * pred movePacman [on, on' : Player->Field] {
 * 	//p.on.id = 1 implies on ++ p->id.2 //getField(2)
 * 	all g: Ghost | not Pacman.on = g.on
 * 	some n: Pacman.on.neighbors | on' = on - Pacman->Pacman.on + Pacman->n
 * }
 * 
 * pred moveGhost [on, on' : Player->Field] {
 * 	all g: Ghost | not Pacman.on = g.on
 * 	all g: Ghost | some n: g.on.neighbors | on' = on - g->g.on + g->n
 * }
 * 
 * 
 * pred show [on', on'' : Player->Field] {
 * 	movePacman [on, on']
 * 	moveGhost [on', on'']
 * 	//move [p, on', on'']
 * }
 * 
 * run show for exactly 1 Ghost, 1 Pacman
 * </pre>
 * 
 * </p>
 */

public final class PacmanModel {
  // private final int field_num = 4;
  // private final int PACMAN_NUM = 1;
  // private final int GHOST_NUM = 1;

  // sigs
  private final Relation Field, TreasureField, Player, Pacman, Ghosts,
      Ghosts_next;
  // fields
  private final Relation Field2Neighbors, Player2On, Player2On_next,
      Ghost2Active, Ghost2Active_next;
  // game data
  private final PacmanGame game;
  // the universe
  private final Universe univ;
  // the bounds
  private final Bounds bounds;
  private final int bitwidth;

  // private final Variable nextOn;

  public PacmanModel(PacmanGame game, int bitwidth) {
    Field = Relation.unary("Field");
    TreasureField = Relation.unary("TreasureField");
    Player = Relation.unary("Player");
    Pacman = Relation.unary("Pacman");
    Ghosts = Relation.unary("Ghost");
    Ghosts_next = Relation.unary("Ghost'");
    // fieldId = Relation.binary("id");
    Field2Neighbors = Relation.binary("neighbors");
    Player2On = Relation.binary("on");
    Player2On_next = Relation.binary("on'");
    Ghost2Active = Relation.binary("active");
    Ghost2Active_next = Relation.binary("active'");
    // nextOn = Variable.nary("on'", 2);
    this.game = game;
    this.bitwidth = bitwidth;
    univ = buildUniverse(game.numFields(), game.numGhosts(),
        game.numTreasures());
    bounds = buildBounds(univ);

  }

  public final Relation fieldRelation() {
    return Field;
  }

  public final Relation treasureFieldRelation() {
    return TreasureField;
  }

  public final Relation playerRelation() {
    return Player;
  }

  public final Relation pacmanRelation() {
    return Pacman;
  }

  public final Relation ghostRelation() {
    return Ghosts;
  }

  public final Relation fieldNeighborsRelation() {
    return Field2Neighbors;
  }

  public final Relation playerOnRelation() {
    return Player2On;

  }

  public Formula decls() {
    Formula onFunctional = Player2On.function(Player, Field);
    Formula on_nextFunctional = Player2On_next.function(Player, Field);
    return onFunctional.and(on_nextFunctional);
  }

  public Map<Relation, TupleSet> initialState() {
    return bounds.lowerBounds();
  }

  /**
   * The returned bounds have the lower bounds of all bounds cleared which are
   * not constant, i.e.,
   * <code>forAll b in this.bounds: b.lowers.containsAll(b.uppers) ? b : new Bounds(b.relation, b.uppers).</code>
   * Generally speaking, every relation that may not change because an element
   * is neither deleted nor created by a graph rule has a its lower and upper
   * bound fixed. All other relations only have an upper bound and an empty
   * lower bound.
   * 
   * @return bounds where lower bounds of all none-exact bounds are cleared
   */
  public Bounds verificationBounds() {
    Bounds vBounds = new Bounds(univ);
    vBounds.boundExactly(TreasureField, bounds.lowerBound(TreasureField));
    vBounds.boundExactly(Field, bounds.lowerBound(Field));
    vBounds.boundExactly(Pacman, bounds.lowerBound(Pacman));
    vBounds.boundExactly(Ghosts, bounds.lowerBound(Ghosts));
    vBounds.boundExactly(Player, bounds.lowerBound(Player));
    vBounds.boundExactly(Field2Neighbors, bounds.lowerBound(Field2Neighbors));

    if (bounds.upperBound(Ghosts_next) != null)
      vBounds.bound(Ghosts_next, bounds.upperBound(Ghosts_next));
    vBounds.bound(Player2On, bounds.upperBound(Player2On_next));
    vBounds.bound(Player2On_next, bounds.upperBound(Player2On_next));
    if (bounds.upperBound(Ghost2Active_next) != null) {
      vBounds.bound(Ghost2Active, bounds.upperBound(Ghost2Active_next));
      vBounds.bound(Ghost2Active_next, bounds.upperBound(Ghost2Active_next));
    }
    // for (Relation r : bounds.relations()) {
    // if (bounds.lowerBound(r).containsAll(bounds.upperBound(r)))
    // vBounds.boundExactly(r, bounds.lowerBound(r));
    // else
    // vBounds.bound(r, bounds.upperBound(r));
    // }
    return vBounds;
  }

  public Bounds bounds() {
    return bounds;
  }

  /**
   * @return
   */
  private Bounds buildBounds(Universe univ) {
    // initialize bounds and tuple factory
    final Bounds bounds = new Bounds(univ);
    final TupleFactory tf = univ.factory();

    buildSignatureRelations(game.numFields(), game.numGhosts(),
        game.numTreasures(), bounds, tf);
    // buildFieldIdRelation(numFields, tf);
    buildNeighbhorsRelation(game.board(), bounds, tf);
    buildPlayerOnRelation(game.pacmanPosition(),
        game.ghostPositions().toArray(new Integer[game.numGhosts()]), bounds,
        tf);

    // bound booleans
    if (bitwidth == 1) {
      bounds.boundExactly(-1, tf.setOf("-1"));
      bounds.boundExactly(0, tf.setOf("0"));
    }

    if (game.numGhosts() > 0) {
      TupleSet ghost2active_lower = tf.setOf(tf.tuple("Ghost0", "-1"),
          tf.tuple("Ghost1", "-1"), tf.tuple("Ghost2", "0"));
      // TupleSet ints = tf.setOf("-1", "0");
      TupleSet ghost2active_upper = tf.noneOf(2);
      for (int i = 0; i < game.numGhosts(); ++i) {
        ghost2active_upper.add(tf.tuple("Ghost" + i, "-1"));
        ghost2active_upper.add(tf.tuple("Ghost" + i, "0"));
      }
      bounds.boundExactly(Ghost2Active, ghost2active_lower);
      bounds.bound(Ghost2Active_next, ghost2active_upper);
    } else {
      bounds.bound(Ghost2Active, tf.noneOf(2));
      bounds.bound(Ghost2Active_next, tf.noneOf(2));
    }

    return bounds;
  }

  /**
   * @param posPacman
   * @param posGhosts
   * @param b
   * @param tf
   */
  private void buildPlayerOnRelation(
      final int posPacman, final Integer[] posGhosts, final Bounds b,
      final TupleFactory tf) {
    // here we define the starting position of Pacman and the Ghost
    final TupleSet on_lower = tf.noneOf(2);
    on_lower.add(tf.tuple("Pacman", "Field" + posPacman));
    for (int i = 0; i < posGhosts.length; i++) {
      on_lower.add(tf.tuple("Ghost" + i, "Field" + posGhosts[i]));
    }
    // next we define all possible Player-Field combinations
    final TupleSet on_upper = b.upperBound(Player).product(b.upperBound(Field));
    // b.bound(on, on_lower, on_upper);
    b.boundExactly(Player2On, on_lower);
    // b.bound(on, on_upper);
    final TupleSet on_next_upper = b.upperBound(Player).product(
        b.upperBound(Field));
    b.bound(Player2On_next, on_next_upper);
  }

  // /**
  // * @param numFields
  // * @param tf
  // */
  // private void buildFieldIdRelation(final int numFields, final TupleFactory
  // tf) {
  // // generate the Field IDs; note that these are not really necessary
  // Collection<Tuple> tuples = new ArrayList<Tuple>(numFields);
  // for (int i = 0; i < numFields; i++) {
  // tuples.add(tf.tuple("Field" + i, String.valueOf(i)));
  // }
  // // b.boundExactly(fieldId, tf.setOf(tuples));
  // }

  /**
   * @param pacmanBoard
   * @param bounds
   * @param tf
   */
  private void buildNeighbhorsRelation(
      PacmanBoard pacmanBoard, final Bounds bounds, final TupleFactory tf) {
    // initialize neighbors relation
    final TupleSet neighbors = tf.noneOf(2);
    for (Entry<Integer, Set<Integer>> entry : game.board().boardStructure()) {
      Integer fieldIndex = entry.getKey();
      Integer srcIndex = game.isTreasureField(fieldIndex) ? game
          .treasureFieldIndexOf(fieldIndex) : fieldIndex;
      String srcFieldLabel = game.isTreasureField(fieldIndex) ? "TreasureField"
          + srcIndex : "Field" + srcIndex;

      TupleSet fNeighbors = tf.noneOf(2);
      for (Integer neighborIndex : entry.getValue()) {
        Integer tgtIndex = game.isTreasureField(neighborIndex) ? game
            .treasureFieldIndexOf(neighborIndex) : neighborIndex;
        String tgtFieldLabel = game.isTreasureField(neighborIndex) ? "TreasureField"
            + tgtIndex
            : "Field" + tgtIndex;
        fNeighbors.add(tf.tuple(srcFieldLabel, tgtFieldLabel));
      }
      neighbors.addAll(fNeighbors);

    }
    // final TupleSet field0Neighbors = tf.noneOf(2);
    // field0Neighbors.add(tf.tuple("Field0", "Field1"));
    // field0Neighbors.add(tf.tuple("Field0", "Field2"));
    // neighbors.addAll(field0Neighbors);
    //
    // final TupleSet field1Neighbors = tf.noneOf(2);
    // field1Neighbors.add(tf.tuple("Field1", "Field0"));
    // // neighbors_field1.add(tf.tuple("Field1"));
    // field1Neighbors.add(tf.tuple("Field1", "TreasureField0"));
    // neighbors.addAll(field1Neighbors);
    //
    // final TupleSet field2Neighbors = tf.noneOf(2);
    // field2Neighbors.add(tf.tuple("Field2", "Field0"));
    // // neighbors_field2.add(tf.tuple("Field2"));
    // field2Neighbors.add(tf.tuple("Field2", "TreasureField0"));
    // neighbors.addAll(field2Neighbors);
    //
    // final TupleSet treasureFieldNeighbors = tf.noneOf(2);
    // treasureFieldNeighbors.add(tf.tuple("TreasureField0", "Field1"));
    // treasureFieldNeighbors.add(tf.tuple("TreasureField0", "Field2"));
    // neighbors.addAll(treasureFieldNeighbors);

    bounds.boundExactly(Field2Neighbors, neighbors);
  }

  /**
   * @param numFields
   * @param numGhosts
   * @param numTreasures
   * @param b
   * @param tf
   */
  private void buildSignatureRelations(
      long numFields, long numGhosts, long numTreasures, final Bounds b,
      final TupleFactory tf) {
    TupleSet fieldTuples = tf.range(tf.tuple("Field0"),
        tf.tuple("Field" + (numFields - numTreasures - 1)));
    fieldTuples.toString();
    if (numTreasures > 0) {
      TupleSet treasureTuples = tf.range(tf.tuple("TreasureField0"),
          tf.tuple("TreasureField" + (numTreasures - 1)));
      b.boundExactly(TreasureField, treasureTuples);
      fieldTuples.addAll(treasureTuples);
    }
    b.boundExactly(Field, fieldTuples);
    b.boundExactly(Pacman, tf.setOf("Pacman"));
    TupleSet playerTuples = tf.setOf("Pacman");
    if (numGhosts > 0) {
      TupleSet ghostTuples = tf.range(tf.tuple("Ghost0"),
          tf.tuple("Ghost" + (numGhosts - 1)));
      b.boundExactly(Ghosts, ghostTuples);
      b.bound(Ghosts_next, ghostTuples);
      playerTuples.addAll(ghostTuples);
    } else {
      b.boundExactly(Ghosts, tf.noneOf(1));
    }
    b.boundExactly(Player, playerTuples);
  }

  /**
   * @param numFields
   * @param numGhosts
   * @param numTreasures
   * @return
   */
  private Universe buildUniverse(
      final long numFields, final long numGhosts, final long numTreasures) {
    final Set<String> atoms = new LinkedHashSet<String>();
    for (int i = 0; i < numFields - numTreasures; i++) {
      atoms.add("Field" + i);
    }
    for (int i = 0; i < numTreasures; i++) {
      atoms.add("TreasureField" + i);
    }
    atoms.add("Pacman");
    for (int i = 0; i < numGhosts; i++) {
      atoms.add("Ghost" + i);
    }
    if (this.bitwidth == 1) {
      // add booleans as integers
      atoms.add("-1"); // represents True
      atoms.add("0"); // represents False
    }
    return new Universe(atoms);
  }

  // private Formula move(Expression nextOn) {
  // // Variable p = Variable.unary("p");
  // // Variable nextPlayerOn = nextPlayerOn;
  // Variable n = Variable.unary("n");
  // Variable g = Variable.unary("g");
  // Formula movePacman = movePacman(nextOn, n).forSome(
  // n.oneOf((pacman.join(on)).join(fieldNeighbors)));
  // Formula moveGhost = moveGhost(nextOn, n, g);
  // return terminiationCondition().and(movePacman.or(moveGhost));
  // }
  //
  // private Formula moveGhost(Expression nextOn, Variable neighbor, Variable
  // ghost) {
  // return
  // nextOn.eq(on.difference(ghost.product(ghost.join(on))).union(ghost.product(neighbor)))
  // .forSome(neighbor.oneOf(ghost.join(on).join(fieldNeighbors))).forSome(ghost.oneOf(ghosts));
  // }
  //
  // private Formula movePacman(Expression nextOn, Variable neighbor) {
  // return nextOn
  // .eq(on.difference(pacman.product(pacman.join(on))).union(pacman.product(neighbor)));
  // }
  //
  // private Formula terminiationCondition() {
  // Variable g = Variable.unary("g");
  // Formula terminationCondition =
  // (pacman.join(on).eq(g.join(on))).not().forAll(g.oneOf(ghosts));
  // return terminationCondition;
  // }
  //
  // private Formula pacmanSurvives(Expression currentOn, Expression nextOn) {
  // Variable g = Variable.unary("g");
  // Formula survival =
  // pacman.join(nextOn).eq(g.join(nextOn)).not().forAll(g.oneOf(ghosts));
  // return survival;
  // }
  //
  // public Formula verificationCondition() {
  // Variable nextOn = Variable.nary("on'", 2);
  // return move(nextOn).and(pacmanSurvives(on, nextOn).not())
  // .forSome(nextOn.setOf(player.product(field))).and(decls());
  // }
  //
  public Formula badProperty() {
    return Pacman.join(this.Player2On).count().gt(IntConstant.constant(1));
  }

  //
  // public Formula onNextIsEmpty() {
  // return on_next.eq(Expression.NONE.product(Expression.NONE));
  // }

  public Formula testDeclaration() {
    Variable n = Variable.unary("n");
    return (n.in(Pacman.join(Player2On).join(Field2Neighbors))).not().forAll(
        n.oneOf(Field));
  }

  public Formula movePacmanOnce() {
    // return movePacman().and(moveGhost());
    return movePacman();
  }

  public Formula moveAndDeactivate() {
    // return movePacman().and(moveGhost());
    return movePacmanAndDeactivateGhost();
  }

  public Formula removeActiveGhosts() {
    Variable g = Variable.unary("g");
    return g.join(Ghost2Active).eq(IntConstant.constant(-1).toExpression())
        .and(Ghosts_next.eq(Ghosts.difference(g))).forSome(g.oneOf(Ghosts));
  }

  public Formula removeActiveGhosts2() {
    Variable g = Variable.unary("g");
    return g.eq((Ghost2Active).join(IntConstant.constant(-1).toExpression()))
        .and(Ghosts_next.eq(Ghosts.difference(g))).forSome(g.setOf(Ghosts));
  }

  /**
   * @return
   */
  private Formula movePacman() {
    Variable n = Variable.unary("n");
    Formula nIsNeighborOfPacmansField = n.in(Pacman.join(Player2On).join(
        Field2Neighbors));
    Variable tField = Variable.unary("tf"); // Treasure Field
    // final Formula pacmanIsNotOnATreasure =
    // ((pacman.join(on)).in(treasureField)).not();
    final Formula pacmanIsNotOnATreasure = (Pacman.join(Player2On)).eq(tField)
        .and(tField.in(TreasureField)).forSome(tField.oneOf(Field)).not();
    Formula if_ = pacmanIsNotOnATreasure.and(nIsNeighborOfPacmansField);
    Formula then_ = Player2On_next.eq((Player2On.union(Pacman.product(n))
        .difference(Pacman.product(Pacman.join(Player2On)))));
    Formula else_ = Player2On_next.eq(Player2On);
    // return Formula.TRUE
    // .and((pacman.join(on).in(treasureField)).not())
    // .and(n.in(pacman.join(on).join(fieldNeighbors)))//
    // .and(n.eq(pacman.join(on)).not())
    // .and((n.eq(Expression.NONE)).not())
    // .and(n.count().eq(IntConstant.constant(1)))
    // .and(on_next.eq(on.union(pacman.product(n))))
    // .forSome(n.oneOf(field));
    return if_.implies(then_).and(if_.not().implies(else_)).forSome(n.oneOf(Field));
    /*
     * if_precondition.and(then_postcondition).or(
     * (if_precondition.not()).and(else_postcondition)).forSome(n.oneOf(field));
     */// forSome(n.oneOf(field));
  }

  /**
   * @return
   */
  private Formula movePacmanAndDeactivateGhost() {
    Variable n = Variable.unary("n");
    Variable g = Variable.unary("g");
    Formula nIsNeighborOfPacmansField = n.in(Pacman.join(Player2On).join(
        Field2Neighbors));
    Variable tField = Variable.unary("tf"); // Treasure Field
    // final Formula pacmanIsNotOnATreasure =
    // ((pacman.join(on)).in(treasureField)).not();
    final Formula pacmanIsNotOnATreasure = (Pacman.join(Player2On)).eq(tField)
        .and(tField.in(TreasureField)).forSome(tField.oneOf(Field)).not();
    Formula if_ = pacmanIsNotOnATreasure.and(nIsNeighborOfPacmansField);
    Formula then_ = Player2On_next.eq(
        (Player2On.difference(Pacman.product(Pacman.join(Player2On)))
            .union(Pacman.product(n)))).and(
        Ghost2Active_next.eq(Ghost2Active.difference(
            g.product(g.join(Ghost2Active))).union(
            g.product(g.join(Ghost2Active)
                .in(IntConstant.constant(-1).toExpression())
                .thenElse(IntConstant.constant(0), IntConstant.constant(-1))
                .toExpression()))));

    Formula else_ = Player2On_next.eq(Player2On).and(
        Ghost2Active_next.eq(Ghost2Active));
    // return Formula.TRUE
    // .and((pacman.join(on).in(treasureField)).not())
    // .and(n.in(pacman.join(on).join(fieldNeighbors)))//
    // .and(n.eq(pacman.join(on)).not())
    // .and((n.eq(Expression.NONE)).not())
    // .and(n.count().eq(IntConstant.constant(1)))
    // .and(on_next.eq(on.union(pacman.product(n))))
    // .forSome(n.oneOf(field));
    return if_.and(then_)
        /* .or(if_.not().and(else_)) */.forSome(
            n.oneOf(Field).and(g.oneOf(Ghosts)));
    /*
     * if_precondition.and(then_postcondition).or(
     * (if_precondition.not()).and(else_postcondition)).forSome(n.oneOf(field));
     */// forSome(n.oneOf(field));
  }

  public Formula pacmanOnTreasureField() {
    return Pacman.join(Player2On_next).in(TreasureField);
  }

  // public Formula assertMovePacman() {
  // Variable nextOn = Variable.nary("on'", 2);
  // Variable n = Variable.unary("n");
  //
  // return movePacman(nextOn,
  // n).forSome(n.oneOf((pacman.join(on)).join(fieldNeighbors))).forSome(
  // nextOn.setOf(player.product(field)));
  // }
  //
  // public Formula assertMovePacmanToTreasure() {
  // Variable nextOn = Variable.nary("on'", 2);
  // Variable n = Variable.unary("n");
  // return movePacman(nextOn, n).forSome(nextOn.setOf(player.product(field)))
  // .and(n.in(treasureField).not()).forSome(n.oneOf((pacman.join(on)).join(fieldNeighbors)));
  // }
}