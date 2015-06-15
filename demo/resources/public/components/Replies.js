var React = require('react');
var sprintf = require('sprintf');

var Reply = React.createClass({
	render: function() {
		var reply = this.props.reply;

		var n = reply.words.length;
		var Words = reply.words.map(function(w, i) {
			return <td key={i}>{w}</td>;
		});

		var S1 = reply.blm_1.spanScores.map(function (s, i) {
			return <td key={i} className="score">{sprintf("%.3f", s)}</td>;
		})
		return (
			<table className="table table-bordered">
				<tbody>
					<tr>{Words}</tr>
					<tr>{S1}</tr>
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