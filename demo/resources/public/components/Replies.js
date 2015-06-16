var React = require('react');
var sprintf = require('sprintf');

function makeRow(o) {
	var offsets = [];
	for(var i=0; i < o.offset || 0; i++) {
		var mykey = o.key + "." + (i+1);
		offsets.push(<td key={mykey}></td>)
	}
	var padding = [];
	for(var i=o.offset; i < o.totalLength - o.colSpan; i ++)
		padding.push(<td />);

	return (
		<tr>{offsets}<td colSpan={o.colSpan}>{o.content}</td>{padding}</tr>
	);
}

var Reply = React.createClass({
	render: function() {
		var reply = this.props.reply;

		var totalLength = reply.words.length;
		var Words = reply.words.map(function(w, i) {
			return <td key={i}><span className="word">{w}</span></td>;
		});

		var scoresView = [];
		["blm_1", "blm_2", "blm_3", "blm_4", "blm_5"].forEach(function(blmName, idx) {
			var blm = reply[blmName];
			var spanSize = idx + 1; // TODO modify reply so server provides this.
			blm.spanScores.forEach(function(score, wordIndex) {
				scoresView.push(makeRow({
					content: (<span>{sprintf("%.3f",score)}</span>),
					offset: wordIndex,
					colSpan: spanSize,
					key: {wordIndex},
					totalLength: totalLength,
				}));
			});
			scoresView.push(<tr>{Words}</tr>);
		});

		return (
			<table className="table table-bordered">
				<tbody>
					<tr>{Words}</tr>
					{scoresView}
				</tbody>
			</table>
		);
	}
});

var Replies = React.createClass({
	render: function() {
		var replies = this.props.replies;
		var replyViews = replies.map(function(reply, i) {
			return (
				<Reply reply={reply} key={i} />
			);
		});

		return (
			<div className="replies">
				{replyViews}
			</div>
		);
	}
});

module.exports = Replies;